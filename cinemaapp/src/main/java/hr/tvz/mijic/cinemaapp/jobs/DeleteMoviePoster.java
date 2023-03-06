package hr.tvz.mijic.cinemaapp.jobs;

import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

@Configuration
public class DeleteMoviePoster {

    @Bean
    public CronTriggerFactoryBean createTriggerForDeletingMoviePoster(@Qualifier("deletingMoviePoster") JobDetail jobDetail) {
        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setJobDetail(jobDetail);
        cronTriggerFactoryBean.setCronExpression("0 0 3 * * ? ");
        return cronTriggerFactoryBean;
    }

    @Bean(name = "deletingMoviePoster")
    public JobDetailFactoryBean createJobDetailFactoryBeanForMoviePoster() {
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(DeleteMoviePosterJob.class);
        jobDetailFactoryBean.setDurability(true);
        return jobDetailFactoryBean;
    }
}
