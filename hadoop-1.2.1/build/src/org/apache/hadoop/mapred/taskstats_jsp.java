package org.apache.hadoop.mapred;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.lang.String;
import java.text.*;
import java.util.*;
import org.apache.hadoop.http.HtmlQuoting;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.JSPUtil.JobWithViewAccessCheck;
import org.apache.hadoop.util.*;
import java.text.SimpleDateFormat;
import org.codehaus.jackson.map.ObjectMapper;

public final class taskstats_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {


/**
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

	private static final long serialVersionUID = 1L;

 
  public static class ErrorResponse {

    private final long errorCode;
    private final String errorDescription;

    // Constructor
    ErrorResponse(long ec, String ed) {

      errorCode = ec;
      errorDescription = ed;
    }

    // Getters
    public long getErrorCode() { return errorCode; }
    public String getErrorDescription() { return errorDescription; }
  }

  public static class TaskStatsResponse {

    public static class TaskCounterGroupInfo {

      public static class TaskCounterInfo {

        private final String name;
        private final long value;

        // Constructor
        TaskCounterInfo(Counters.Counter c) {

          name = c.getDisplayName();
          value = c.getCounter();
        }

        // Getters
        public String getName() { return name; }
        public long getValue() { return value; }
      }

      private final String groupName;
      private final Collection<TaskCounterInfo> taskCountersInfo;

      // Constructor
      TaskCounterGroupInfo(String gn) { 
        
        groupName = gn; 
      
        taskCountersInfo = new ArrayList<TaskCounterInfo>();
      }

      // To add one TaskCounterInfo object at a time.
      void addTaskCounterInfo(TaskCounterInfo tci) { taskCountersInfo.add(tci); }

      // Getters
      public String getGroupName() { return groupName; }
      public Collection<TaskCounterInfo> getTaskCountersInfo() { return taskCountersInfo; }
    }

    private final String taskId;
    private final Collection<TaskCounterGroupInfo> taskCounterGroupsInfo;

    // Constructor
    TaskStatsResponse(String ti) {

      taskId = ti;

      taskCounterGroupsInfo = new ArrayList<TaskCounterGroupInfo>();
    }

    // To add one TaskCounterGroupInfo object at a time.
    void addTaskCounterGroupInfo(TaskCounterGroupInfo tcgi) { taskCounterGroupsInfo.add(tcgi); }

    // Getters
    public String getTaskId() { return taskId; }
    public Collection<TaskCounterGroupInfo> getTaskCounterGroupsInfo() { return taskCounterGroupsInfo; }
  }

  private static java.util.List _jspx_dependants;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    JspFactory _jspxFactory = null;
    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      _jspxFactory = JspFactory.getDefaultFactory();
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');
      out.write('\n');

  String response_format = request.getParameter("format");

  if (response_format != null) {
    /* Eventually, the HTML output should also be driven off of these *Response
     * objects. 
     * 
     * Someday. 
     */
    TaskStatsResponse theTaskStatsResponse = null;
    ErrorResponse theErrorResponse = null;

    JobTracker tracker = (JobTracker) application.getAttribute("job.tracker");
    String attemptid = request.getParameter("attemptid");
    String tipid = request.getParameter("tipid");

    /* Make sure at least one of the 2 possible query parameters were specified. */
    if ((attemptid != null) || (tipid != null)) {

      TaskAttemptID attemptidObj = TaskAttemptID.forName(attemptid);

      // Obtain tipid for attemptId, if attemptId is available.
      TaskID tipidObj =
          (attemptidObj == null) ? TaskID.forName(tipid)
                                 : attemptidObj.getTaskID();
      if (tipidObj != null) {

        // Obtain jobid from tipid
        final JobID jobidObj = tipidObj.getJobID();
        String jobid = jobidObj.toString();
        
        JobWithViewAccessCheck myJob = JSPUtil.checkAccessAndGetJob(tracker, jobidObj,
            request, response);

        /* Proceed only if the user is authorized to view this job. */
        if (myJob.isViewJobAllowed()) {

          JobInProgress job = myJob.getJob();

          if (job != null) {

            Counters counters;

            if (attemptid == null) {
              counters = tracker.getTipCounters(tipidObj);
              attemptid = tipidObj.toString();
            }
            else {
              TaskStatus taskStatus = tracker.getTaskStatus(attemptidObj);
              counters = taskStatus.getCounters();
            }

            if (counters != null) {

              theTaskStatsResponse = new TaskStatsResponse(attemptid);

              for (String groupName : counters.getGroupNames()) {

                Counters.Group group = counters.getGroup(groupName);

                TaskStatsResponse.TaskCounterGroupInfo taskCounterGroupInfo = 
                  new TaskStatsResponse.TaskCounterGroupInfo(group.getDisplayName());

                for (Counters.Counter counter : group) {

                  taskCounterGroupInfo.addTaskCounterInfo
                    (new TaskStatsResponse.TaskCounterGroupInfo.TaskCounterInfo
                      (counter));
                }

                theTaskStatsResponse.addTaskCounterGroupInfo(taskCounterGroupInfo);
              }
            } else {
              response.setStatus(HttpServletResponse.SC_NOT_FOUND);
              theErrorResponse = new ErrorResponse(4103, "Counters For " + attemptid + " Not Found");
            }
          } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            theErrorResponse = new ErrorResponse(4102, "JobID " + jobid + " Not Found");
          }
        } else {

          /* TODO XXX Try and factor out JSPUtil.setErrorAndForward() for re-use
           * (and to not make it blindly dispatch to job_authorization_error.jsp,
           * which is all HTML. 
           */

          /* TODO XXX Make this return JSON, not HTML. */

          /* Nothing to do here, since JSPUtil.setErrorAndForward() has already been 
           * called from inside JSPUtil.checkAccessAndGetJob().
           */
        }
      } else {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        theErrorResponse = new ErrorResponse(4101, 
          ((attemptid == null) ? ("TipID " + tipid) : ("AttemptID " + attemptid)) + " Invalid");
      }
    } else {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      theErrorResponse = new ErrorResponse(4100, "Missing TipID/AttemptID");
    }

    /* ------------ Response generation begins here ------------ */

    /* For now, "json" is the only supported format. 
     *
     * As more formats are supported, this should become a cascading 
     * if-elsif-else block.
     */
    if ("json".equals(response_format)) {

      response.setContentType("application/json");

      ObjectMapper responseObjectMapper = new ObjectMapper();
      /* A lack of an error response implies we have a meaningful 
       * application response? Why not!
       */
      out.println(responseObjectMapper.writeValueAsString
        ((theErrorResponse == null) ? theTaskStatsResponse : theErrorResponse));

    } else {
      response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
    }
  } else {

      out.write('\n');

  // Spit out HTML only in the absence of the "format" query parameter.
  response.setContentType("text/html; charset=UTF-8");

  JobTracker tracker = (JobTracker) application.getAttribute("job.tracker");
  String attemptid = request.getParameter("attemptid");
  TaskAttemptID attemptidObj = TaskAttemptID.forName(attemptid);
  // Obtain tipid for attemptId, if attemptId is available.
  TaskID tipidObj =
      (attemptidObj == null) ? TaskID.forName(request.getParameter("tipid"))
                             : attemptidObj.getTaskID();
  // Obtain jobid from tipid
  final JobID jobidObj = tipidObj.getJobID();
  String jobid = jobidObj.toString();
  
  JobWithViewAccessCheck myJob = JSPUtil.checkAccessAndGetJob(tracker, jobidObj,
      request, response);
  if (!myJob.isViewJobAllowed()) {
    return; // user is not authorized to view this job
  }

  JobInProgress job = myJob.getJob();
  if (job == null) {
    out.print("<b>Job " + jobid + " not found.</b><br>\n");
    return;
  }
  
  Format decimal = new DecimalFormat();
  Counters counters;
  if (attemptid == null) {
    counters = tracker.getTipCounters(tipidObj);
    attemptid = tipidObj.toString(); // for page title etc
  }
  else {
    TaskStatus taskStatus = tracker.getTaskStatus(attemptidObj);
    counters = taskStatus.getCounters();
  }

      out.write("\n\n<!DOCTYPE html>\n<html>\n  <head>\n    <title>Counters for ");
      out.print(attemptid);
      out.write("</title>\n  </head>\n<body>\n<h1>Counters for ");
      out.print(attemptid);
      out.write("</h1>\n\n<hr>\n\n");

  if ( counters == null ) {

      out.write("\n    <h3>No counter information found for this task</h3>\n");

  } else {    

      out.write("\n    <table>\n");

      for (String groupName : counters.getGroupNames()) {
        Counters.Group group = counters.getGroup(groupName);
        String displayGroupName = group.getDisplayName();

      out.write("\n        <tr>\n          <td colspan=\"3\"><br/><b>\n          ");
      out.print(HtmlQuoting.quoteHtmlChars(displayGroupName));
      out.write("</b></td>\n        </tr>\n");

        for (Counters.Counter counter : group) {
          String displayCounterName = counter.getDisplayName();
          long value = counter.getCounter();

      out.write("\n          <tr>\n            <td width=\"50\"></td>\n            <td>");
      out.print(HtmlQuoting.quoteHtmlChars(displayCounterName));
      out.write("</td>\n            <td align=\"right\">");
      out.print(decimal.format(value));
      out.write("</td>\n          </tr>\n");

        }
      }

      out.write("\n    </table>\n");

  }

      out.write("\n\n<hr>\n<a href=\"jobdetails.jsp?jobid=");
      out.print(jobid);
      out.write("\">Go back to the job</a><br>\n<a href=\"jobtracker.jsp\">Go back to JobTracker</a><br>\n");

out.println(ServletUtil.htmlFooter());

      out.write('\n');

} // if (response_format != null) 

    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      if (_jspxFactory != null) _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
