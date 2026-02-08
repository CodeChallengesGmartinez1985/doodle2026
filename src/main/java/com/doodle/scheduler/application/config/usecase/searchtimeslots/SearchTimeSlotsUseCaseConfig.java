package com.doodle.scheduler.application.config.usecase.searchtimeslots;

import com.doodle.scheduler.application.config.usecase.searchtimeslots.decorators.LoggedSearchTimeSlotsUseCaseDecorator;
import com.doodle.scheduler.application.config.usecase.searchtimeslots.decorators.TransactionalSearchTimeSlotsUseCaseDecorator;
import com.doodle.scheduler.application.domain.calendar.port.in.searchtimeslots.SearchTimeSlotsUseCase;
import com.doodle.scheduler.application.domain.calendar.port.out.searchtimeslots.SearchTimeSlotsPort;
import com.doodle.scheduler.application.domain.calendar.service.SearchTimeSlotsServiceImpl;
import com.doodle.scheduler.application.domain.user.port.out.LoadUserByUsernamePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class SearchTimeSlotsUseCaseConfig {

    @Bean
    public SearchTimeSlotsUseCase coreSearchTimeSlotsUseCase(
            LoadUserByUsernamePort loadUserByUsernamePort,
            SearchTimeSlotsPort searchTimeSlotsPort) {
        return new SearchTimeSlotsServiceImpl(loadUserByUsernamePort, searchTimeSlotsPort);
    }

    @Bean
    public SearchTimeSlotsUseCase transactionalSearchTimeSlotsUseCase(
            @Qualifier("coreSearchTimeSlotsUseCase") SearchTimeSlotsUseCase core) {
        return new TransactionalSearchTimeSlotsUseCaseDecorator(core);
    }

    @Bean
    public SearchTimeSlotsUseCase loggedSearchTimeSlotsUseCase(
            @Qualifier("transactionalSearchTimeSlotsUseCase") SearchTimeSlotsUseCase transactional) {
        return new LoggedSearchTimeSlotsUseCaseDecorator(transactional);
    }

    @Bean
    @Primary
    public SearchTimeSlotsUseCase searchTimeSlotsUseCase(
            @Qualifier("loggedSearchTimeSlotsUseCase") SearchTimeSlotsUseCase logged) {
        return logged;
    }
}
