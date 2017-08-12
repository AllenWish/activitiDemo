package com.allen.activitiDemo;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ActivitiTest {
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
     * 生成25张表
     */
    @Test
    public void testCreateTableWithXml(){
        // 引擎配置
        ProcessEngineConfiguration pec=ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        // 获取流程引擎对象
        ProcessEngine processEngine=pec.buildProcessEngine();
    }
    

    
    /**
	 * 获取默认的流程引擎实例 会自动读取activiti.cfg.xml文件 
	 */
	private ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
	  /**
     * 部署流程定义
     */
    @Test
    public void deploy(){
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
                .complete("15004"); // 指定要完成的任务ID
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
    

    /**
     * 通过流程部署ID获取流程图图片
     */
    @Test
    public void getImageById()throws Exception{
        InputStream inputStream=processEngine.getRepositoryService()
            .getResourceAsStream("10001", "helloWorld.png"); // 根据流程部署ID和资源名称获取输入流
        FileUtils.copyInputStreamToFile(inputStream, new File("D:/helloWorld.png"));
    }
    
    
    
    
    /**
     * 查询最新版本的流程定义
     */
    @Test
    public void listLastVersion()throws Exception{
         
        // 获取流程定义集合，根据Key升序排序
        List<org.activiti.engine.repository.ProcessDefinition> listAll=processEngine.getRepositoryService() // 获取service类
                .createProcessDefinitionQuery() // 创建流程定义查询
                .orderByProcessDefinitionVersion().asc() // 根据流程定义版本升序
                .list();
        // 定义有序Map 相同的key 假如添加map的值 后面的值会覆盖前面相同key的值
        Map<String,org.activiti.engine.repository.ProcessDefinition> map=new LinkedHashMap<String,org.activiti.engine.repository.ProcessDefinition>();
        // 遍历集合 根据key来覆盖前面的值 来保证最新的Key覆盖前面的所有老的Key的值 
        for(org.activiti.engine.repository.ProcessDefinition pd:listAll){
            map.put(pd.getKey(), pd);
        }
        List<org.activiti.engine.repository.ProcessDefinition> pdList=new LinkedList<org.activiti.engine.repository.ProcessDefinition>(map.values());
        for(org.activiti.engine.repository.ProcessDefinition pd:pdList){
            System.out.println("ID_："+pd.getId());
            System.out.println("NAME_："+pd.getName());
            System.out.println("KEY_："+pd.getKey());
            System.out.println("VERSION_："+pd.getVersion());
            System.out.println("===================");
        }
    }

    /**
     * 删除所有Key相同的流程定义
     * @throws Exception
     */
    @Test
    public void deleteByKey()throws Exception{
        List<ProcessDefinition> pdList=processEngine.getRepositoryService()  // 获取service类
                .createProcessDefinitionQuery() // 创建流程定义查询
                .processDefinitionKey("mySecondProcess") // 根据Key查询
                .list();
        for(ProcessDefinition pd:pdList){  // 遍历集合 获取流程定义的每个部署Id，根据这个id来删除所有流程定义
            processEngine.getRepositoryService()
            .deleteDeployment(pd.getDeploymentId(), true); 
        }
    }

    /**
     * 查询流程状态（正在执行 or 已经执行结束）
     */
    @Test
    public void processState(){
        ProcessInstance pi=processEngine.getRuntimeService() // 获取运行时Service
            .createProcessInstanceQuery() // 创建流程实例查询
            .processInstanceId("25001") // 用流程实例ID查询
            .singleResult();
        if(pi!=null){
            System.out.println("流程正在执行！");
        }else{
            System.out.println("流程已经执行结束！");
        }
    }
    
    /**
     * 历史任务查询
     */
    @Test
    public void historyTaskList(){
        List<HistoricTaskInstance> list=processEngine.getHistoryService() // 历史任务Service
                .createHistoricTaskInstanceQuery() // 创建历史任务实例查询
                .taskAssignee("AllenWish") // 指定办理人
                //.finished() // 查询已经完成的任务  
                //.unfinished()//查询未完成任务
                .list();
        for(HistoricTaskInstance hti:list){
            System.out.println("任务ID:"+hti.getId());
            System.out.println("流程实例ID:"+hti.getProcessInstanceId());
            System.out.println("班里人："+hti.getAssignee());
            System.out.println("创建时间："+hti.getCreateTime());
            System.out.println("结束时间："+hti.getEndTime());
            System.out.println("===========================");
        }
    }
    
    
    /**
     * 查询历史流程实例
     */
    @Test
    public void getHistoryProcessInstance(){
        HistoricProcessInstance hpi= processEngine.getHistoryService() // 历史任务Service
            .createHistoricProcessInstanceQuery() // 创建历史流程实例查询
            .processInstanceId("25001") // 指定流程实例ID
            .singleResult();
        System.out.println("流程实例ID:"+hpi.getId());
        System.out.println("创建时间："+hpi.getStartTime());
        System.out.println("结束时间："+hpi.getEndTime());
    }
    
    
    /**
     * 历史活动查询
     */
    @Test
    public void historyActInstanceList(){
        List<HistoricActivityInstance> list=processEngine.getHistoryService() // 历史任务Service
                .createHistoricActivityInstanceQuery() // 创建历史活动实例查询
                .processInstanceId("35001") // 指定流程实例id
                .finished() // 查询已经完成的任务  
                .list();
        for(HistoricActivityInstance hai:list){
            System.out.println("任务ID:"+hai.getId());
            System.out.println("流程实例ID:"+hai.getProcessInstanceId());
            System.out.println("活动名称："+hai.getActivityName());
            System.out.println("办理人："+hai.getAssignee());
            System.out.println("开始时间："+hai.getStartTime());
            System.out.println("结束时间："+hai.getEndTime());
            System.out.println("===========================");
        }
    }
    
}
