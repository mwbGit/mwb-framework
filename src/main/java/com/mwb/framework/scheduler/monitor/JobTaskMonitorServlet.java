package com.mwb.framework.scheduler.monitor;

import com.mwb.framework.log.Log;
import com.mwb.framework.scheduler.AbstractStatefulJobTask;
import com.mwb.framework.scheduler.dao.JobTaskExecutionStatus;
import com.mwb.framework.scheduler.dao.JobTaskExecutionStatusDao;
import com.mwb.framework.scheduler.dao.QZJobDetail;
import com.mwb.framework.scheduler.dao.QZJobDetailDao;
import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

public class JobTaskMonitorServlet extends HttpServlet implements ApplicationContextAware {
	private static final long serialVersionUID = 1L;

	private static final Log LOG = Log.getLog(JobTaskMonitorServlet.class);
	
	@Autowired
	private JobTaskExecutionStatusDao schedulerExecutionDao;
	
	@Autowired
	private QZJobDetailDao qzJobDetailDao;

	@Autowired
	private Scheduler scheduler;

	private ApplicationContext applicationContext;
	
	private String passwordFile;
	
	private String password;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {			
			
			Map<?, ?> parameterMap = req.getParameterMap();

            if (parameterMap == null || parameterMap.isEmpty()) {
            	
            	PrintWriter writer = new PrintWriter(new OutputStreamWriter(resp.getOutputStream()));

            	resp.setContentType("text/html;charset=UTF-8");
            	writer.println("<html>");
            	writer.println("<body>");
            	
            	//table1: list all job tasks in spring and quartz table
            	writer.println("<table border=\"1\" align=\"center\"><caption>任务对比列表（Spring VS Quartz）</caption>");
            	writer.println("<tr>");
            	writer.println("<td><b>" + "Spring任务类" + "</b></td>");
            	writer.println("<td><b>" + "Quartz任务类" + "</b></td>");
            	writer.println("<td><b>" + "任务名称" + "</b></td>");
            	writer.println("<td><b>" + "任务组" + "</b></td>");
            	writer.println("<td><b>" + "任务描述" + "</b></td>");
            	writer.println("</tr>");
                
            	List<QZJobDetail> qzTasks = qzJobDetailDao.selectAllQuartzJobTask();
            	String[] springDefinedTasks = getAllSpringDefinedSchedulerTask();
            	Arrays.sort(springDefinedTasks);
            	
            	int i = 0;
            	List<QZJobDetail> allTasks = new ArrayList<QZJobDetail>();
            	for (i = 0; i < springDefinedTasks.length; i ++) {
            		String colorRed = "";
            		
            		if (i >= qzTasks.size() || !springDefinedTasks[i].equals(qzTasks.get(i).getJobName())) {
            			colorRed = "color=\"red\"";
            		} else {
            			allTasks.add(qzTasks.get(i));
            		}
      
            		writer.println("<tr>");
            		writer.println("<td><font " + colorRed + ">" + ((springDefinedTasks[i] == null) ? "" : springDefinedTasks[i]) + "</font></td>");
            		if(i < qzTasks.size()) {
            			writer.println("<td><font " + colorRed + ">" + ((qzTasks.get(i).getJobClassName() == null) ? "" : qzTasks.get(i).getJobClassName()) + "</font></td>");
            			writer.println("<td><font " + colorRed + ">" + ((qzTasks.get(i).getJobName() == null) ? "" : qzTasks.get(i).getJobName()) + "</font></td>");
            			writer.println("<td><font " + colorRed + ">" + ((qzTasks.get(i).getJobGroup() == null) ? "" : qzTasks.get(i).getJobGroup()) + "</font></td>");
            			writer.println("<td><font " + colorRed + ">" + ((qzTasks.get(i).getDescription() == null) ? "" : qzTasks.get(i).getDescription()) + "</font></td>");
            		} else {
            			writer.println("<td></td>");
            			writer.println("<td></td>");
            			writer.println("<td></td>");
            		}
            		writer.println("</tr>");
            	}
            	
            	while(i < qzTasks.size()) {
            		writer.println("<td><font color=\"red\">" + ((qzTasks.get(i).getJobClassName() == null) ? "" : qzTasks.get(i).getJobClassName()) + "</font></td>");
        			writer.println("<td><font color=\"red\">" + ((qzTasks.get(i).getJobName() == null) ? "" : qzTasks.get(i).getJobName()) + "</font></td>");
        			writer.println("<td><font color=\"red\">" + ((qzTasks.get(i).getJobGroup() == null) ? "" : qzTasks.get(i).getJobGroup()) + "</font></td>");
        			writer.println("<td><font color=\"red\">" + ((qzTasks.get(i).getDescription() == null) ? "" : qzTasks.get(i).getDescription()) + "</font></td>");
        			i++;
            	}
            	writer.println("</table>");
            	
            	writer.println("<br>");
            	writer.println("<br>");
            	
            	//table2: list all job task to execute manually
            	List<JobTaskDetail> sts = getAllJobTask(allTasks);
            	writer.println("<table border=\"1\" align=\"center\"><caption>任务详情列表</caption>");
            	writer.println("<tr>");
            	writer.println("<td><b>" + "任务名称" + "</b></td>");
            	writer.println("<td><b>" + "任务别名" + "</b></td>");
            	writer.println("<td><b>" + "Cron表达式" + "</b></td>");
            	writer.println("<td><b>" + "任务描述" + "</b></td>");
            	writer.println("<td><b>" + "最近一次执行时间" + "</b></td>");
            	writer.println("<td><b>" + "最近一次执行时长（单位毫秒）" + "</b></td>");
            	writer.println("<td><b>" + "最近一次执行是否成功" + "</b></td>");
            	writer.println("<td><b>" + "是否支持手动执行" + "</b></td>");
            	writer.println("<td><b>" + "手动执行" + "</b></td>");
            	writer.println("</tr>");
                
            	for(JobTaskDetail st : sts) {
            		writer.println("<tr>");
            		writer.println("<td name=\"name\">" + ((st.getName() == null) ? "" : st.getName()) + "</td>");
            		writer.println("<td name=\"alias\">" + ((st.getAlias() == null) ? "" : st.getAlias()) + "</td>");
            		writer.println("<td name=\"cronExpression\">" + ((st.getCronExpression() == null) ? "" : st.getCronExpression()) + "</td>");
            		writer.println("<td name=\"description\">" + ((st.getDescription() == null) ? "" : st.getDescription()) + "</td>");
            		writer.println("<td name=\"executeTime\">" + ((st.getExecuteTime() == null) ? "" : st.getExecuteTime()) + "</td>");
            		writer.println("<td name=\"executeDuration\">" + st.getLastExecutionDuration()+ "</td>");
            		writer.println("<td name=\"executeSuccess\">" + (st.isLastExecutionSucess()) + "</td>");
            		writer.println("<td name=\"manualSupported\">" + (st.isManualSupported())  + "</td>");
            		if (st.isManualSupported()) {
            			String formId = st.getName() + st.getGroupName() + "_manuulyExecution";
            			writer.println("<td>");
            			writer.println("<button onclick=\"disp_prompt(\'" + formId + "\');\">手动执行</button>");
            			writer.println("<form id= \""+ formId +"\" action=\"\\JobTaskMonitorServlet\" method=\"get\" type=\"hidden\">"
                        		+ "<input name=\"taskName\" type=\"hidden\" value=\"" + st.getName() + "\">"
                        		+ "<input name=\"groupName\" type=\"hidden\" value=\"" + st.getGroupName() + "\">"
                        		+ "</form>");
            			writer.println("</td>");
                    }
            		writer.println("</tr>");
                }
            	writer.println("</table>");
    			
            	//密码验证
            	writer.println("<script type=\"text/javascript\">");
            	writer.println("function disp_prompt(formId){");
            	writer.println("var password=prompt(\"请输入密码\",\"\")");
            	writer.println("if (password!=null && password!=\"\")");
            	writer.println("{");
            	writer.println("if (document.getElementById(\"password\").value == password)");
            	writer.println("{");
            	writer.println("document.getElementById(formId).submit();");
            	writer.println("} else {");
            	writer.println("alert(\"密码错误！\")");
            	writer.println("}");
            	writer.println("}");
            	writer.println("}");
            	writer.println("</script>");
    			
            	writer.println("<input id=\"password\" type=\"hidden\" value=\"" + password + "\"></input>");
            	writer.println("</html>");
            	writer.println("</body>");
            	writer.flush();
            } else {
            	String taskName = req.getParameter("taskName");
            	String groupName = req.getParameter("groupName");
            	manualExecuteJobTask(taskName, groupName);
            	
            	resp.sendRedirect(req.getRequestURI());     
            }
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}	
	
	@Override
	public void init() throws ServletException {
		super.init();
		
		try {
			if (!StringUtils.isBlank(passwordFile)) {
				File file = new File(passwordFile);
		        if(file.exists()){
		            FileReader reader = new FileReader(file);
		            BufferedReader bufferReader = new BufferedReader(reader);
		            String line = null;
		            if ((line = bufferReader.readLine())!=null){
		                password = line.trim();
		            }
		            bufferReader.close();
		        }
			}
		} catch(Exception e) {
			throw new ServletException(e);
		}
	}

	private boolean initJobTask(JobTaskDetail jobTaskDetail) throws Exception {
		
		String taskBeanName = jobTaskDetail.getName();
		
		AbstractStatefulJobTask jobTask = (AbstractStatefulJobTask)applicationContext.getBean(taskBeanName);
		
		if(!jobTask.isPersistentSupported()) {
			return false;
		}
		
		jobTaskDetail.setAlias(jobTask.getAlias());
		jobTaskDetail.setDescription(jobTask.getDescription());
		jobTaskDetail.setLastExecutionDuration(0);
		jobTaskDetail.setLastExecutionSucess(false);
		jobTaskDetail.setExecuteTime(null);
		jobTaskDetail.setManualSupported(jobTask.isManualSupported());
		jobTaskDetail.setName(jobTask.getName());
		
		Trigger[] triggers = scheduler.getTriggersOfJob(jobTaskDetail.getName(), jobTaskDetail.getGroupName());
		if (triggers != null) {
			StringBuilder sb = new StringBuilder();
			for (int i=0; i<triggers.length; i++) {
				Trigger trigger = triggers[i];
				if (trigger instanceof CronTrigger) { // 保存CronTrigger类型定时任务的cron表达式
					String cronExpression = ((CronTrigger) trigger).getCronExpression();
					if (i>0) {
						sb.append(", ");	
					}
					sb.append(cronExpression);
				}
			}
			jobTaskDetail.setCronExpression(sb.toString());
		} 
		return true;
	}
	

	private void manualExecuteJobTask(String taskName, String groupName) {		
		try {
			JobTaskExecutionStatus lastExecutionStatus = schedulerExecutionDao.selectJobTaskExecutionStatusByNameAndGroupName(taskName, groupName);
			Date lastExecuteTime = null;
			if (lastExecutionStatus != null) {
				lastExecuteTime = lastExecutionStatus.getExecuteTime();
			}
			
			JobDetail jobDetail = scheduler.getJobDetail(taskName, groupName);
			if (jobDetail != null) {		
				JobDataMap jobDataMap = new JobDataMap();
				
				scheduler.triggerJob(taskName, groupName, jobDataMap);
			} else {
				String msg = String.format("The task:%s, group:%s doesn't exist", taskName, groupName);
				LOG.error("manual exexute scheduler task error:{}.", msg);
			}
	
			// 获取手动执行结果
			String taskBeanName = jobDetail.getName();
			AbstractStatefulJobTask jobTask = (AbstractStatefulJobTask)applicationContext.getBean(taskBeanName);
			
			if (jobTask.isPersistentSupported()) { // 执行结果需要支持持久化
				JobTaskExecutionStatus thisJobTaskExecutionStatus = schedulerExecutionDao.selectJobTaskExecutionStatusByNameAndGroupName(taskName, groupName);
				String executingMsg = String.format("The task:%s, group:%s is executing for manual.", taskName, groupName);
				String failedMsg = String.format("The task:%s, group:%s execute failed for manual.", taskName, groupName);
				
				int loopTimes = 0;
				int loopLimit = 3; // 循环次数上限
				long sleepMillis = 500l;
				if (lastExecuteTime == null) { //定时任务从来没有执行过
					while (thisJobTaskExecutionStatus == null && loopTimes < loopLimit) { // 循环3次获取执行结果
						loopTimes++;
						if (loopTimes >= loopLimit) {
							break;
						}
						Thread.sleep(sleepMillis);
						thisJobTaskExecutionStatus = schedulerExecutionDao.selectJobTaskExecutionStatusByNameAndGroupName(taskName, groupName);
					}
					
					if (thisJobTaskExecutionStatus == null) {
						LOG.info("manual exexute scheduler task error:{}.", executingMsg);
					} else {
						if (!thisJobTaskExecutionStatus.getIsLastExecutionSucess().getValue()) {
							LOG.error("manual exexute scheduler task error:{}.", failedMsg);
						}
					}
					
				} else { // 定时任务在这之前已执行过
					Date thisExecuteTime = thisJobTaskExecutionStatus.getExecuteTime();
					while (thisExecuteTime.equals(lastExecuteTime) && loopTimes < loopLimit) {
						loopTimes++;
						if (loopTimes >= loopLimit) {
							break;
						}
						Thread.sleep(sleepMillis);
						thisJobTaskExecutionStatus = schedulerExecutionDao.selectJobTaskExecutionStatusByNameAndGroupName(taskName, groupName);
					}
					
					if (thisExecuteTime.equals(lastExecuteTime)) {
						LOG.info("manual exexute scheduler task error:{}.", executingMsg);
					} else {
						if (!thisJobTaskExecutionStatus.getIsLastExecutionSucess().getValue()) {
							LOG.error("manual exexute scheduler task error:{}.", failedMsg);
						}
					}
				}
			} else {// 执行结果不支持持久化
				String failedMsg = String.format("The task:%s, group:%s doesn't suppport persistence.", taskName, groupName);
				LOG.error("manual exexute scheduler task error:{}.", failedMsg);
			}
		}catch(Exception e) {
			LOG.error("manual exexute scheduler task error:{}.", e.getMessage());
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	private List<JobTaskDetail> getAllJobTask(List<QZJobDetail> allTasks) throws Exception {
		List<JobTaskDetail> nos = new ArrayList<JobTaskDetail>();

		List<QZJobDetail> details = allTasks;
		for(QZJobDetail detail : details) {
			JobTaskExecutionStatus task = schedulerExecutionDao.selectJobTaskExecutionStatusByNameAndGroupName(detail.getJobName(), detail.getJobGroup());
			JobTaskDetail jobTaskDetail = null;
			if(task == null) {
				jobTaskDetail = new JobTaskDetail(detail.getJobName(), detail.getJobGroup());
				if(initJobTask(jobTaskDetail)) {
					nos.add(jobTaskDetail);
				}
			} else {
				jobTaskDetail = new JobTaskDetail(task);
				nos.add(jobTaskDetail);
			}
		}
		
		return nos;
	}
	
	private String[] getAllSpringDefinedSchedulerTask() {
		return applicationContext.getBeanNamesForType(AbstractStatefulJobTask.class);
	}

	public void setPasswordFile(String passwordFile) {
		this.passwordFile = passwordFile;
	}
	
	private class JobTaskDetail {
		private String name;
		private String alias;

		private String groupName;
		private String description;
		private String cronExpression;

		private boolean manualSupported;
		private boolean isLastExecutionSucess;

		private int LastExecutionDuration;
		private Date executeTime; 

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getGroupName() {
			return groupName;
		}

		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getCronExpression() {
			return cronExpression;
		}

		public void setCronExpression(String cronExpression) {
			this.cronExpression = cronExpression;
		}

		public boolean isManualSupported() {
			return manualSupported;
		}

		public void setManualSupported(boolean manualSupported) {
			this.manualSupported = manualSupported;
		}

		public boolean isLastExecutionSucess() {
			return isLastExecutionSucess;
		}

		public void setLastExecutionSucess(boolean isLastExecutionSucess) {
			this.isLastExecutionSucess = isLastExecutionSucess;
		}

		public int getLastExecutionDuration() {
			return LastExecutionDuration;
		}

		public void setLastExecutionDuration(int lastExecutionDuration) {
			LastExecutionDuration = lastExecutionDuration;
		}

		public Date getExecuteTime() {
			return executeTime;
		}

		public void setExecuteTime(Date executeTime) {
			this.executeTime = executeTime;
		}
		
		public JobTaskDetail(JobTaskExecutionStatus jobTaskExecutionStatus) {
			this.setAlias(jobTaskExecutionStatus.getAlias());
			this.setCronExpression(jobTaskExecutionStatus.getCronExpression());
			this.setDescription(jobTaskExecutionStatus.getDescription());
			this.setExecuteTime(jobTaskExecutionStatus.getExecuteTime());
			this.setGroupName(jobTaskExecutionStatus.getGroupName());
			this.setLastExecutionDuration(jobTaskExecutionStatus.getLastExecutionDuration());
			this.setLastExecutionSucess(jobTaskExecutionStatus.getIsLastExecutionSucess().getValue());
			this.setManualSupported(jobTaskExecutionStatus.getManualSupported().getValue());
			this.setName(jobTaskExecutionStatus.getName());
		}

		public JobTaskDetail(String jobName, String jobGroup) {
			this.setGroupName(jobGroup);
			this.setName(jobName);
		}
	};
}
