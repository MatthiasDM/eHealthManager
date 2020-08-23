/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gcms_ehealthmanager;

import gcms_ehealthmanager.database.DatabaseActions;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
//import gcms.database.DatabaseActions;

public class MainServletContextListener implements javax.servlet.ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        //Notification that the servlet context is about to be shut down.   
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        // do all the tasks that you need to perform just after the server starts
        DatabaseActions.connect();
        DatabaseActions.createDatabaseMap();
        //Notification that the web application initialization process is starting
    }
}
