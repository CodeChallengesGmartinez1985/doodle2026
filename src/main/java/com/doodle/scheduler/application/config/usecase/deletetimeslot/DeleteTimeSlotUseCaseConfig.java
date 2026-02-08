package com.doodle.scheduler.application.config.usecase.deletetimeslot;

import com.doodle.scheduler.application.config.usecase.deletetimeslot.decorators.LoggedDeleteTimeSlotUseCaseDecorator;
import com.doodle.scheduler.application.config.usecase.deletetimeslot.decorators.TransactionalDeleteTimeSlotUseCaseDecorator;
import com.doodle.scheduler.application.domain.calendar.port.in.DeleteTimeSlotUseCase;
import com.doodle.scheduler.application.domain.calendar.port.out.DeleteTimeSlotPort;
import com.doodle.scheduler.application.domain.calendar.port.out.LoadTimeSlotByIdPort;
import com.doodle.scheduler.application.domain.calendar.service.DeleteTimeSlotServiceImpl;
import com.doodle.scheduler.application.domain.user.port.out.LoadUserByUsernamePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class DeleteTimeSlotUseCaseConfig {

    @Bean
    public DeleteTimeSlotUseCase coreDeleteTimeSlotUseCase(
            LoadUserByUsernamePort loadUserByUsernamePort,
            LoadTimeSlotByIdPort loadTimeSlotByIdPort,
            DeleteTimeSlotPort deleteTimeSlotPort) {
        return new DeleteTimeSlotServiceImpl(loadUserByUsernamePort, loadTimeSlotByIdPort, deleteTimeSlotPort);
    }

    @Bean
    public DeleteTimeSlotUseCase transactionalDeleteTimeSlotUseCase(
            @Qualifier("coreDeleteTimeSlotUseCase") DeleteTimeSlotUseCase core) {
        return new TransactionalDeleteTimeSlotUseCaseDecorator(core);
    }

    @Bean
    public DeleteTimeSlotUseCase loggedDeleteTimeSlotUseCase(
            @Qualifier("transactionalDeleteTimeSlotUseCase") DeleteTimeSlotUseCase transactional) {
        return new LoggedDeleteTimeSlotUseCaseDecorator(transactional);
    }

    @Bean
    @Primary
    public DeleteTimeSlotUseCase deleteTimeSlotUseCase(
            @Qualifier("loggedDeleteTimeSlotUseCase") DeleteTimeSlotUseCase logged) {
        return logged;
    }
}
