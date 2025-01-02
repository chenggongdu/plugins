package com.youlu.plugin.mybatis;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;

public class MyBatisTestGeneratorAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        VirtualFile file = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE);
        if (file == null || !file.getName().endsWith(".xml")) {
            return;
        }

        try {
            PsiFile psiFile = project.getService(com.intellij.psi.PsiManager.class).findFile(file);
            if (!(psiFile instanceof XmlFile)) {
                return;
            }

            XmlFile xmlFile = (XmlFile) psiFile;
            XmlTag rootTag = xmlFile.getRootTag();
            if (rootTag == null || !"mapper".equals(rootTag.getName())) {
                return;
            }

            String namespace = rootTag.getAttributeValue("namespace");
            if (namespace == null) {
                return;
            }

            generateTestXmlFile(project, file, namespace);

            generateTestJavaClass(project, file, namespace);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void generateTestXmlFile(Project project, VirtualFile file, String namespace) throws IOException {
        String sourcePath = file.getPath();
        String mainSourcePath = sourcePath.substring(0, sourcePath.indexOf("/src/main/java"));
        String testResourcesPath = mainSourcePath + "/src/test/resources";
        File testXmlDir = new File(testResourcesPath, "mybatis");
        if (!testXmlDir.exists()) {
            testXmlDir.mkdirs();
        }


        String testXmlFileName = "Test" + file.getName();
        File testXmlFile = new File(testXmlDir, testXmlFileName);
        if (testXmlFile.exists()) {
            // If the test file already exists, it will not be generated
            return;
        }

        String xmlSourceRootPath = sourcePath.substring(sourcePath.indexOf("/src/main/java") + 15);
        String testXmlContent = generateTestXmlContent(testResourcesPath, xmlSourceRootPath);


        Files.write(testXmlFile.toPath(), testXmlContent.getBytes(StandardCharsets.UTF_8));

        com.intellij.openapi.vfs.VfsUtil.markDirtyAndRefresh(true, true, true, testXmlFile);
    }

    private String generateTestXmlContent(String testResourcesPath, String xmlSourceRootPath) {
        Properties dbConfig = loadDatabaseConfig(testResourcesPath);
        String databaseUrl = dbConfig.getProperty("database.url", "jdbc:mysql://localhost:3306/default_db");
        String username = dbConfig.getProperty("database.username", "default_user");
        String password = dbConfig.getProperty("database.password", "default_password");

        String testXmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<!DOCTYPE configuration\n" +
                "        PUBLIC \"-//mybatis.org//DTD Config 3.0//EN\"\n" +
                "        \"http://mybatis.org/dtd/mybatis-3-config.dtd\">\n" +
                "<configuration>\n" +
                "    <environments default=\"default\">\n" +
                "        <environment id=\"default\">\n" +
                "            <transactionManager type=\"JDBC\"/>\n" +
                "            <dataSource type=\"UNPOOLED\">\n" +
                "                <property name=\"driver\" value=\"com.mysql.jdbc.Driver\"/>\n" +
                "                <property name=\"url\"\n" +
                "                          value=\"" + databaseUrl + "\"/>\n" +
                "                <property name=\"username\" value=\"" + username + "\"/>\n" +
                "                <property name=\"password\" value=\"" + password + "\"/>\n" +
                "            </dataSource>\n" +
                "        </environment>\n" +
                "    </environments>\n" +
                "   <mappers>\n" +
                "        <mapper resource=\"" + xmlSourceRootPath + "\"/>\n" +
                "    </mappers>\n" +
                "</configuration>";
        return testXmlContent;
    }

    private Properties loadDatabaseConfig(String testResourcesPath) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(testResourcesPath.concat("/database-config.properties"))) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return properties;
    }


    private void generateTestJavaClass(Project project, VirtualFile file, String namespace) throws IOException {
        String sourcePath = file.getPath();
        String mainSourcePath = sourcePath.substring(0, sourcePath.indexOf("/src/main/java"));
        File testMapperDir = new File(mainSourcePath + "/src/test/java", "com/youlu/mapper");
        if (!testMapperDir.exists()) {
            testMapperDir.mkdirs();
        }

        String className = "Test" + namespace.substring(namespace.lastIndexOf('.') + 1);
        File testClassFile = new File(testMapperDir, className + ".java");
        if (testClassFile.exists()) {
            // If the test file already exists, it will not be generated
            return;
        }

        String xmlFileName = "Test" + file.getName();
        String testClassContent = generateTestClassContent(className, xmlFileName, namespace);

        Files.write(testClassFile.toPath(), testClassContent.getBytes(StandardCharsets.UTF_8));

        com.intellij.openapi.vfs.VfsUtil.markDirtyAndRefresh(true, true, true, testClassFile);
    }


    private String generateTestClassContent(String className, String xmlFileName, String mapperClassName) {
        String mapperSimpleName = mapperClassName.substring(mapperClassName.lastIndexOf('.') + 1);  // 获取类名部分

        String testClassContent = "package com.youlu.mapper;\n\n" +
                "import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;\n" +
                "import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;\n" +
                "import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;\n" +
                "import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;\n" +
                "import " + mapperClassName + ";\n" +
                "import org.apache.ibatis.session.SqlSessionFactory;\n" +
                "import org.junit.BeforeClass;\n\n" +
                "public class " + className + " {\n" +
                "    private static " + mapperClassName + " " + mapperSimpleName + ";\n\n" +
                "    @BeforeClass\n" +
                "    public static void setUpMyBatisDatabase() {\n" +
                "        // Create SqlSessionFactory\n" +
                "        SqlSessionFactory builder = new MybatisSqlSessionFactoryBuilder().build(" + className + ".class.getClassLoader().\n" +
                "                getResourceAsStream(\"mybatis/" + xmlFileName + "\"));\n\n" +
                "        // Add MyBatis-Plus Plugin\n" +
                "        final MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();\n" +
                "        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());\n" +
                "        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());\n" +
                "        builder.getConfiguration().addInterceptor(interceptor);\n\n" +
                "        // Fetch Mapper\n" +
                "        " + mapperSimpleName + " = builder.getConfiguration().getMapper(" + mapperClassName + ".class, builder.openSession(true));\n" +
                "    }\n" +
                "}";

        return testClassContent;
    }

}
