package com.allen.activitiDemo;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class ActitvitiTest {
	/**
	 * 获取默认的流程引擎实例 会自动读取activiti.cfg.xml文件 
	 */
	private ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
	 /**
     * 生成25张Activiti表
     */
    @Test
    public void testCreateTable() {
        // 引擎配置
        ProcessEngineConfiguration pec=ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        pec.setJdbcDriver("com.mysql.jdbc.Driver");
        pec.setJdbcUrl("jdbc:mysql://localhost:3306/db_activiti");
        pec.setJdbcUsername("root");
        pec.setJdbcPassword("1234");
         
        /**
         * false 不能自动创建表
         * create-drop 先删除表再创建表
         * true 自动创建和更新表  
         */
        pec.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
         
        // 获取流程引擎对象
        ProcessEngine processEngine=pec.buildProcessEngine();
    }
    
    /**
     * 使用xml配置 简化
     */
    @Test
    public void testCreateTableWithXml(){
        // 引擎配置
        ProcessEngineConfiguration pec=ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        // 获取流程引擎对象
        ProcessEngine processEngine=pec.buildProcessEngine();
    }
    
    
    
    /**
     * 部署流程定义
     */
    @Test
    public void deploy(){
        // 获取部署对象
        Deployment deployment=processEngine.getRepositoryService() // 部署Service
                     .createDeployment()  // 创建部署
                     .addClasspathResource("diagrams/helloworld.bpmn")  // 加载资源文件
                     .addClasspathResource("diagrams/helloworld.png")   // 加载资源文件
                     .name("HelloWorld流程")  // 流程名称
                     .deploy(); // 部署
        System.out.println("流程部署ID:"+deployment.getId());
        System.out.println("流程部署Name:"+deployment.getName());
    }
    
    /**
     * 启动流程实例
     * 运行start方法；
     * 启动流程，数据库流程运行表也会发生相应的变化；
     * 首先是运行时流程任务表：act_ru_task；插入了一条任务数据；
     * 接下来是act_ru_execution 运行时流程执行表；
     * 接下来是act_ru_identitylink 是于执行主体相关信息表；
     */
    @Test
    public void start(){
        // 启动并获取流程实例
        ProcessInstance processInstance=processEngine.getRuntimeService() // 运行时流程实例Service
            .startProcessInstanceByKey("myProcess"); // 流程定义表的KEY字段值
        System.out.println("流程实例ID:"+processInstance.getId());
        System.out.println("流程定义ID:"+processInstance.getProcessDefinitionId());
    }
    
    /**
     * 查看任务
     * 运行findTask方法，控制台输出；
     */
    @Test
    public void findTask(){
        // 查询并且返回任务即可
        List<Task> taskList=processEngine.getTaskService() // 任务相关Service
                .createTaskQuery()  // 创建任务查询
                .taskAssignee("AllenWish") // 指定某个人
                .list(); 
        for(Task task:taskList){
            System.out.println("任务ID:"+task.getId());
            System.out.println("任务名称："+task.getName());
            System.out.println("任务创建时间："+task.getCreateTime());
            System.out.println("任务委派人："+task.getAssignee());
            System.out.println("流程实例ID:"+task.getProcessInstanceId());
        }
    }
    /**
     * 完成任务
     * 我们继续走流程 执行completeTask方法；
     * 执行完后，流程其实就已经走完了。
     */
    @Test
    public void completeTask(){
        processEngine.getTaskService() // 任务相关Service
                .complete("7504"); // 指定要完成的任务ID
    }
    /**
     * 首先ru开头的运行时候所有表的数据都没了，因为现在流程都走完了。不需要那些数据了；
     * 然后在hi开头的表里，存了不少数据，主要是用来归档查询用的；
     * 这里不一一截图，大概来解析下，后面也会具体讲到。helloWorld仅仅是让大家有一个大体的认识；
     * act_hi_taskinst 历史流程实例任务表加了一条任务数据；
     * act_hi_procinst 历史流程实例实例表加了一条流程实例相关信息的数据（包括开始时间，结束时间等等信息）；
     * act_hi_identitylink 历史流程实例参数者的表加了一条数据；
     * act_hi_actinst 历史活动节点表加了三条流程活动节点信息的数据（每个流程实例具体的执行活动节点的信息）；
     */
}
