package org.apache.hadoop.mapred;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.text.DecimalFormat;
import org.apache.hadoop.http.HtmlQuoting;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public final class tasktracker_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      response.setContentType("text/html; charset=UTF-8");
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

  TaskTracker tracker = (TaskTracker) application.getAttribute("task.tracker");
  String trackerName = tracker.getName();

      out.write("\n\n<!DOCTYPE html>\n<html>\n\n<title>");
      out.print( trackerName );
      out.write(" Task Tracker Status</title>\n\n<body>\n<h1>");
      out.print( trackerName );
      out.write(" Task Tracker Status</h1>\n<img src=\"/static/hadoop-logo.jpg\"/><br>\n<b>Version:</b> ");
      out.print( VersionInfo.getVersion());
      out.write(",\n                r");
      out.print( VersionInfo.getRevision());
      out.write("<br>\n<b>Compiled:</b> ");
      out.print( VersionInfo.getDate());
      out.write(" by \n                 ");
      out.print( VersionInfo.getUser());
      out.write("<br>\n\n<h2>Running tasks</h2>\n<center>\n<table border=2 cellpadding=\"5\" cellspacing=\"2\">\n<tr><td align=\"center\">Task Attempts</td><td>Status</td>\n    <td>Progress</td><td>Errors</td></tr>\n\n  ");

     Iterator itr = tracker.getRunningTaskStatuses().iterator();
     while (itr.hasNext()) {
       TaskStatus status = (TaskStatus) itr.next();
       out.print("<tr><td>" + status.getTaskID());
       out.print("</td><td>" + status.getRunState()); 
       out.print("</td><td>" + 
                 StringUtils.formatPercent(status.getProgress(), 2));
       out.print("</td><td><pre>" +
           HtmlQuoting.quoteHtmlChars(status.getDiagnosticInfo()) +
           "</pre></td>");
       out.print("</tr>\n");
     }
  
      out.write("\n</table>\n</center>\n\n<h2>Non-Running Tasks</h2>\n<table border=2 cellpadding=\"5\" cellspacing=\"2\">\n<tr><td align=\"center\">Task Attempts</td><td>Status</td>\n  ");

    for(TaskStatus status: tracker.getNonRunningTasks()) {
      out.print("<tr><td>" + status.getTaskID() + "</td>");
      out.print("<td>" + status.getRunState() + "</td></tr>\n");
    }
  
      out.write("\n</table>\n\n\n<h2>Tasks from Running Jobs</h2>\n<center>\n<table border=2 cellpadding=\"5\" cellspacing=\"2\">\n<tr><td align=\"center\">Task Attempts</td><td>Status</td>\n    <td>Progress</td><td>Errors</td></tr>\n\n  ");

     itr = tracker.getTasksFromRunningJobs().iterator();
     while (itr.hasNext()) {
       TaskStatus status = (TaskStatus) itr.next();
       out.print("<tr><td>" + status.getTaskID());
       out.print("</td><td>" + status.getRunState()); 
       out.print("</td><td>" + 
                 StringUtils.formatPercent(status.getProgress(), 2));
       out.print("</td><td><pre>" +
           HtmlQuoting.quoteHtmlChars(status.getDiagnosticInfo()) +
           "</pre></td>");
       out.print("</tr>\n");
     }
  
      out.write("\n</table>\n</center>\n\n\n<h2>Local Logs</h2>\n<a href=\"/logs/\">Log</a> directory\n\n");

out.println(ServletUtil.htmlFooter());

      out.write('\n');
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