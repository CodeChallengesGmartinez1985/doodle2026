package com.doodle.scheduler.application.config.usecase.createtimeslot;

import com.doodle.scheduler.application.config.usecase.createtimeslot.decorators.LoggedCreateTimeSlotUseCaseDecorator;
import com.doodle.scheduler.application.config.usecase.createtimeslot.decorators.TransactionalCreateTimeSlotUseCaseDecorator;
import com.doodle.scheduler.application.domain.port.in.CreateTimeSlotUseCase;
import com.doodle.scheduler.application.domain.port.out.timeslot.LoadTimeSlotsByUserPort;
import com.doodle.scheduler.application.domain.port.out.timeslot.SaveTimeSlotPort;
import com.doodle.scheduler.application.domain.port.out.user.LoadUserByUsernamePort;
import com.doodle.scheduler.application.domain.service.calendar.CreateTimeSlotServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class CreateTimeSlotUseCaseConfig {

    @Bean
    public CreateTimeSlotUseCase coreCreateTimeSlotUseCase(
            LoadUserByUsernamePort loadUserByUsernamePort,
            LoadTimeSlotsByUserPort loadTimeSlotsByUserPort,
            SaveTimeSlotPort saveTimeSlotPort) {
        return new CreateTimeSlotServiceImpl(loadUserByUsernamePort, loadTimeSlotsByUserPort, saveTimeSlotPort);
    }

    @Bean
    public CreateTimeSlotUseCase transactionalCreateTimeSlotUseCase(
            @Qualifier("coreCreateTimeSlotUseCase") CreateTimeSlotUseCase core) {
        return new TransactionalCreateTimeSlotUseCaseDecorator(core);
    }

    @Bean
    public CreateTimeSlotUseCase loggedCreateTimeSlotUseCase(
            @Qualifier("transactionalCreateTimeSlotUseCase") CreateTimeSlotUseCase transactional) {
        return new LoggedCreateTimeSlotUseCaseDecorator(transactional);
    }

    @Bean
    @Primary
    public CreateTimeSlotUseCase createTimeSlotUseCase(
            @Qualifier("loggedCreateTimeSlotUseCase") CreateTimeSlotUseCase logged) {
        return logged;
    }
}
