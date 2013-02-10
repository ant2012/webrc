package net.ant.rc.web; /**
 * Created with IntelliJ IDEA.
 * User: Ant
 * Date: 04.02.13
 * Time: 21:58
 * To change this template use File | Settings | File Templates.
 */

import net.ant.rc.serial.*;
import net.ant.rc.serial.arduino2wd.Arduino2WDSerialCommunicator;
import net.ant.rc.serial.exception.CommPortException;
import net.ant.rc.serial.exception.UnsupportedHardwareException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.PriorityBlockingQueue;

@WebListener()
public class WebRCContextHolder implements ServletContextListener,
        HttpSessionListener, HttpSessionAttributeListener {

    SerialCommunicatorInterface serialCommunicator;
    SerialService serialService;

    // Public constructor is required by servlet spec
    public WebRCContextHolder() {
    }

    // -------------------------------------------------------
    // ServletContextListener implementation
    // -------------------------------------------------------
    public void contextInitialized(ServletContextEvent sce) {
      /* This method is called when the servlet context is
         initialized(when the Web application is deployed). 
         You can initialize servlet context related data here.
      */
        ServletContext servletContext = sce.getServletContext();

        try {
            //Выбор конкретного драйвера
            SerialCommunicator sc = new SerialCommunicator();
            //sc.disconnect();
            if (sc.getChassisType() == sc.CHASSIS_TYPE_ARDUINO_2WD)
                this.serialCommunicator = new Arduino2WDSerialCommunicator(sc);
            //Add new hardware here

            //servletContext.setAttribute("SerialCommunicator", this.serialCommunicator);

            PriorityBlockingQueue<VectorCommand> commandQueue = new PriorityBlockingQueue<>();
            servletContext.setAttribute("CommandQueue", commandQueue);

            this.serialService = new SerialService(this.serialCommunicator, commandQueue);
            Thread serialServiceThread = new Thread(this.serialService);
            serialServiceThread.start();
        } catch (CommPortException | UnsupportedHardwareException e) {
            e.printStackTrace();
        }


    }

    public void contextDestroyed(ServletContextEvent sce) {
      /* This method is invoked when the Servlet Context 
         (the Web application) is undeployed or 
         Application Server shuts down.
      */
        if(this.serialCommunicator!=null)
            this.serialCommunicator.disconnect();
        if(this.serialService != null)
            this.serialService.stop();
    }

    // -------------------------------------------------------
    // HttpSessionListener implementation
    // -------------------------------------------------------
    public void sessionCreated(HttpSessionEvent se) {
      /* Session is created. */
    }

    public void sessionDestroyed(HttpSessionEvent se) {
      /* Session is destroyed. */
    }

    // -------------------------------------------------------
    // HttpSessionAttributeListener implementation
    // -------------------------------------------------------

    public void attributeAdded(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute 
         is added to a session.
      */
    }

    public void attributeRemoved(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute
         is removed from a session.
      */
    }

    public void attributeReplaced(HttpSessionBindingEvent sbe) {
      /* This method is invoked when an attibute
         is replaced in a session.
      */
    }
}
