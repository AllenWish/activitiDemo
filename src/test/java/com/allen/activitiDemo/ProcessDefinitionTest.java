package com.allen.activitiDemo;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.junit.Test;

public class ProcessDefinitionTest {
	/**
     * 获取默认的流程引擎实例 会自动读取activiti.cfg.xml文件 
     */
    private ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
     
    /**
     * 部署流程定义使用classpath方式
     */
    @Test
    public void deployWithClassPath(){
        // 获取部署对象
        Deployment deployment=processEngine.getRepositoryService() // 部署Service
                     .createDeployment()  // 创建部署
                     .addClasspathResource("diagrams/allen.bpmn")  // 加载资源文件
                     .addClasspathResource("diagrams/allen.png")   // 加载资源文件
                     .name("allen流程")  // 流程名称
                     .deploy(); // 部署
        System.out.println("流程部署ID:"+deployment.getId());
        System.out.println("流程部署Name:"+deployment.getName());
    }
    
    
    /**
     * 部署流程定义使用zip方式
     */
    @Test
    public void deployWithZip(){
        InputStream inputStream=this.getClass()  // 获取当前class对象
                            .getClassLoader()   // 获取类加载器
                            .getResourceAsStream("diagrams/allen.zip"); // 获取指定文件资源流
        ZipInputStream zipInputStream=new ZipInputStream(inputStream); // 实例化zip输入流对象
        // 获取部署对象
        Deployment deployment=processEngine.getRepositoryService() // 部署Service
                     .createDeployment()  // 创建部署
                     .name("allen流程2")  // 流程名称
                     .addZipInputStream(zipInputStream)  // 添加zip是输入流
                     .deploy(); // 部署
        System.out.println("流程部署ID:"+deployment.getId());
        System.out.println("流程部署Name:"+deployment.getName());
    }
    
    
    
    
    
    
    /**
     * 查询流程定义 返回流程定义集合 ---对应act_re_procdef
     */
    @Test
    public void list(){
        List<org.activiti.engine.repository.ProcessDefinition> pdList=processEngine.getRepositoryService() // 获取service类
            .createProcessDefinitionQuery() // 创建流程定义查询
            .processDefinitionKey("allenProcess") // 通过key查询
            .list(); // 返回一个集合
        for(org.activiti.engine.repository.ProcessDefinition pd:pdList){
            System.out.println("ID_："+pd.getId());
            System.out.println("NAME_："+pd.getName());
            System.out.println("KEY_："+pd.getKey());
            System.out.println("VERSION_："+pd.getVersion());
            System.out.println("===================");
        }
    }
    
    
    /**
     * 通过ID查询当个流程定义
     */
    @Test
    public void getById(){
    	org.activiti.engine.repository.ProcessDefinition pd=processEngine.getRepositoryService() // 获取service类
                .createProcessDefinitionQuery() // 创建流程定义查询
                .processDefinitionId("allenProcess:2:20004") // 通过id查询
                .singleResult(); // 查询返回当个结果
        System.out.println("ID_："+pd.getId());
        System.out.println("NAME_："+pd.getName());
        System.out.println("KEY_："+pd.getKey());
        System.out.println("VERSION_："+pd.getVersion());
    }
    
    /**
     * 删除流程定义
     * 假如这个流程定义的流程实例在运行活动中，未完结。
     * 这时候我们执行上面的代码，会报错
     */
    @Test
    public void delete(){
        processEngine.getRepositoryService()
            .deleteDeployment("12501"); // 流程部署ID
        System.out.println("delete OK！");
    }
    /**
     * 级联删除 已经在使用的流程实例信息也会被级联删除
     */
    @Test
    public void deleteCascade(){
        processEngine.getRepositoryService()
            .deleteDeployment("12501", true); // 默认是false true就是级联删除
        System.out.println("delete cascade OK!");
    }
}
