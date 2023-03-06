package hr.tvz.mijic.cinemaapp.jobs;

import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

@Configuration
public class DeleteIncompleteReservation {

    @Bean
    public CronTriggerFactoryBean createTriggerForDeletingReservation(@Qualifier("deletingReservation") JobDetail jobDetail) {
        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setJobDetail(jobDetail);
        cronTriggerFactoryBean.setCronExpression("0 */2 * * * ? ");
        return cronTriggerFactoryBean;
    }

    @Bean(name = "deletingReservation")
    public JobDetailFactoryBean createJobDetailFactoryBeanForDeletingReservation() {
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(DeleteIncomleteReservationJob.class);
        jobDetailFactoryBean.setDurability(true);
        return jobDetailFactoryBean;
    }
}
