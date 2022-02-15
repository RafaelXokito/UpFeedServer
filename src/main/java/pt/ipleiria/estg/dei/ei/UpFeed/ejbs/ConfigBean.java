package pt.ipleiria.estg.dei.ei.UpFeed.ejbs;

import pt.ipleiria.estg.dei.ei.UpFeed.entities.Administrator;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Startup
@Singleton
public class ConfigBean {

    @EJB
    AdministratorBean administratorBean;

    @PostConstruct
    public void populateDB() {
        System.out.println("Starting Point!");

        System.out.println("# Administrators ");
        System.out.println("## Creating Administrators ");
        long adminRafael = administratorBean.create("RafaelPereira@hotmail.com","1234","Rafael Pereira");
        System.out.println("## Updating Administrators ");
        administratorBean.update(adminRafael,"RafaelMendesPereira@hotmail.com","Rafael Mendes Pereira");
        System.out.println("## Deleting Administrators ");
        administratorBean.delete(adminRafael);

    }


}
