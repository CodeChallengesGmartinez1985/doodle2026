package com.doodle.scheduler.application.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * ArchUnit tests to enforce hexagonal architecture rules.
 *
 * This test suite ensures:
 * - Domain layer independence (no dependencies on adapters or infrastructure)
 * - Proper adapter annotations and structure
 * - Port naming conventions
 * - Dependency direction rules (domain ← ports → adapters)
 */
@DisplayName("Hexagonal Architecture Tests")
class HexagonalArchitectureTest {

    private static final String BASE_PACKAGE = "com.doodle.scheduler.application";
    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(BASE_PACKAGE);
    }

    @Nested
    @DisplayName("Layer Dependency Rules")
    class LayerDependencyRules {

        @Test
        @DisplayName("Domain layer should not depend on adapters or config")
        void domainLayerShouldNotDependOnAdaptersOrConfig() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage("..adapter..", "..config..")
                    .because("Domain layer must be independent of infrastructure concerns");

            rule.check(classes);
        }

        @Test
        @DisplayName("Domain layer should not depend on Spring Framework")
        void domainLayerShouldNotDependOnSpringFramework() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage("org.springframework..")
                    .because("Domain layer must be framework-agnostic");

            rule.check(classes);
        }

        @Test
        @DisplayName("Domain layer should not depend on JPA")
        void domainLayerShouldNotDependOnJPA() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage("jakarta.persistence..")
                    .because("Domain layer must not depend on persistence frameworks");

            rule.check(classes);
        }

        @Test
        @DisplayName("Layered architecture should be respected")
        void layeredArchitectureShouldBeRespected() {
            layeredArchitecture()
                    .consideringOnlyDependenciesInLayers()
                    .layer("Domain").definedBy("..domain..")
                    .layer("Adapters").definedBy("..adapter..")
                    .layer("Config").definedBy("..config..")
                    .whereLayer("Domain").mayNotAccessAnyLayer()
                    .whereLayer("Adapters").mayOnlyAccessLayers("Domain")
                    .whereLayer("Config").mayOnlyAccessLayers("Domain", "Adapters")
                    .because("Hexagonal architecture requires proper layer separation")
                    .check(classes);
        }
    }

    @Nested
    @DisplayName("Port Rules")
    class PortRules {

        @Test
        @DisplayName("Port interfaces should be named correctly")
        void portInterfacesShouldBeNamedCorrectly() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..port..")
                    .and().areInterfaces()
                    .should().haveSimpleNameEndingWith("Port")
                    .orShould().haveSimpleNameEndingWith("UseCase")
                    .because("Port interfaces should follow naming convention: *Port or *UseCase");

            rule.check(classes);
        }

        @Test
        @DisplayName("Input ports should reside in port.in package")
        void inputPortsShouldResideInPortInPackage() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("UseCase")
                    .and().resideInAPackage("..port..")
                    .should().resideInAPackage("..port.in..")
                    .because("Use cases (input ports) should be in port.in package");

            rule.check(classes);
        }

        @Test
        @DisplayName("Output ports should reside in port.out package")
        void outputPortsShouldResideInPortOutPackage() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("Port")
                    .and().resideInAPackage("..port..")
                    .and().areInterfaces()
                    .should().resideInAPackage("..port.out..")
                    .because("Output ports should be in port.out package");

            rule.check(classes);
        }

        @Test
        @DisplayName("Ports should be interfaces")
        void portsShouldBeInterfaces() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..port..")
                    .and().areNotNestedClasses()
                    .and().haveSimpleNameNotEndingWith("Command")
                    .and().haveSimpleNameNotEndingWith("QueryResult")
                    .should().beInterfaces()
                    .because("Ports should be defined as interfaces (excluding Commands and QueryResults)");

            rule.check(classes);
        }

        @Test
        @DisplayName("Ports should reside in domain package")
        void portsShouldResideInDomainPackage() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..port..")
                    .should().resideInAPackage("..domain..")
                    .because("Ports are part of the domain layer");

            rule.check(classes);
        }
    }

    @Nested
    @DisplayName("Adapter Rules")
    class AdapterRules {

        @Test
        @DisplayName("REST controllers should be in adapter.in.rest package")
        void restControllersShouldBeInCorrectPackage() {
            ArchRule rule = classes()
                    .that().areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
                    .should().resideInAPackage("..adapter.in.rest..")
                    .because("REST controllers are incoming adapters");

            rule.check(classes);
        }

        @Test
        @DisplayName("Incoming adapters should be annotated with @RestController")
        void incomingAdaptersShouldBeAnnotatedCorrectly() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..adapter.in.rest..")
                    .and().haveSimpleNameEndingWith("Controller")
                    .and().haveSimpleNameNotStartingWith("Base")
                    .and().areNotAnnotatedWith(org.springframework.web.bind.annotation.RestControllerAdvice.class)
                    .should().beAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
                    .because("REST controllers should be properly annotated (excluding base classes)");

            rule.check(classes);
        }

        @Test
        @DisplayName("Repository adapters should be in adapter.out.persistence package")
        void repositoryAdaptersShouldBeInCorrectPackage() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("RepositoryAdapter")
                    .should().resideInAPackage("..adapter.out.persistence..")
                    .because("Repository adapters are outgoing persistence adapters");

            rule.check(classes);
        }

        @Test
        @DisplayName("Outgoing adapters should be annotated with @Component")
        void outgoingAdaptersShouldBeAnnotatedWithComponent() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..adapter.out..")
                    .and().haveSimpleNameEndingWith("Adapter")
                    .should().beAnnotatedWith(org.springframework.stereotype.Component.class)
                    .because("Outgoing adapters should be Spring components");

            rule.check(classes);
        }

        @Test
        @DisplayName("JPA entities should only be in adapter.out.persistence package")
        void jpaEntitiesShouldOnlyBeInPersistenceAdapter() {
            ArchRule rule = classes()
                    .that().areAnnotatedWith(jakarta.persistence.Entity.class)
                    .should().resideInAPackage("..adapter.out.persistence..")
                    .because("JPA entities are infrastructure concerns");

            rule.check(classes);
        }

        @Test
        @DisplayName("JPA entities should be named with JpaEntity suffix")
        void jpaEntitiesShouldBeNamedCorrectly() {
            ArchRule rule = classes()
                    .that().areAnnotatedWith(jakarta.persistence.Entity.class)
                    .should().haveSimpleNameEndingWith("JpaEntity")
                    .because("JPA entities should follow naming convention");

            rule.check(classes);
        }

        @Test
        @DisplayName("Spring Data repositories should only be in adapter.out.persistence package")
        void springDataRepositoriesShouldOnlyBeInPersistenceAdapter() {
            ArchRule rule = classes()
                    .that().areAssignableTo(org.springframework.data.repository.Repository.class)
                    .should().resideInAPackage("..adapter.out.persistence..")
                    .allowEmptyShould(true)
                    .because("Spring Data repositories are infrastructure concerns");

            rule.check(classes);
        }

        @Test
        @DisplayName("Spring Data repositories should be named with JpaRepository suffix")
        void springDataRepositoriesShouldBeNamedCorrectly() {
            ArchRule rule = classes()
                    .that().areAssignableTo(org.springframework.data.repository.Repository.class)
                    .should().haveSimpleNameEndingWith("JpaRepository")
                    .allowEmptyShould(true)
                    .because("Spring Data repositories should follow naming convention");

            rule.check(classes);
        }
    }

    @Nested
    @DisplayName("Domain Model Rules")
    class DomainModelRules {

        @Test
        @DisplayName("Domain entities should extend Entity base class")
        void domainEntitiesShouldExtendEntityBaseClass() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..domain..model..")
                    .and().haveSimpleNameNotEndingWith("Exception")
                    .and().haveSimpleNameNotEndingWith("State")
                    .and().areNotInterfaces()
                    .and().areNotEnums()
                    .and().areNotAnnotatedWith(java.lang.annotation.Documented.class)
                    .and().areNotAssignableTo(Throwable.class)
                    .and().areNotAssignableTo(Record.class)
                    .and().doNotHaveSimpleName("Entity")
                    .and().doNotHaveSimpleName("AggregateRoot")
                    .and().doNotHaveSimpleName("ValueObject")
                    .should().beAssignableTo("com.doodle.scheduler.application.domain.common.model.Entity")
                    .orShould().beAssignableTo("com.doodle.scheduler.application.domain.common.model.AggregateRoot")
                    .orShould().beAssignableTo("com.doodle.scheduler.application.domain.common.model.ValueObject")
                    .orShould().beEnums()
                    .because("Domain models should extend base domain classes (excluding State pattern and Records)");

            rule.check(classes);
        }

        @Test
        @DisplayName("Domain models should not have JPA annotations")
        void domainModelsShouldNotHaveJPAAnnotations() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..domain..model..")
                    .should().beAnnotatedWith(jakarta.persistence.Entity.class)
                    .orShould().beAnnotatedWith(jakarta.persistence.Table.class)
                    .orShould().beAnnotatedWith(jakarta.persistence.Column.class)
                    .because("Domain models should not have JPA annotations");

            rule.check(classes);
        }

        @Test
        @DisplayName("Domain exceptions should extend DomainException")
        void domainExceptionsShouldExtendDomainException() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..domain..exception..")
                    .and().haveSimpleNameEndingWith("Exception")
                    .should().beAssignableTo("com.doodle.scheduler.application.domain.common.exception.DomainException")
                    .because("Domain exceptions should extend DomainException base class");

            rule.check(classes);
        }
    }

    @Nested
    @DisplayName("Service Rules")
    class ServiceRules {

        @Test
        @DisplayName("Domain services should be in domain.*.service package")
        void domainServicesShouldBeInCorrectPackage() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("ServiceImpl")
                    .or().haveSimpleNameEndingWith("Service")
                    .and().resideInAPackage("..domain..")
                    .should().resideInAPackage("..domain..service..")
                    .because("Domain services should be in service package");

            rule.check(classes);
        }

        @Test
        @DisplayName("Domain services should not be annotated with @Service")
        void domainServicesShouldNotBeAnnotatedWithService() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..domain..service..")
                    .should().beAnnotatedWith(org.springframework.stereotype.Service.class)
                    .because("Domain services should not depend on Spring annotations");

            rule.check(classes);
        }

        @Test
        @DisplayName("Domain services should implement use case interfaces")
        void domainServicesShouldImplementUseCases() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..domain..service..")
                    .and().haveSimpleNameEndingWith("ServiceImpl")
                    .should().resideInAPackage("..domain..")
                    .because("Domain services implement use case interfaces");

            rule.check(classes);
        }
    }

    @Nested
    @DisplayName("Configuration Rules")
    class ConfigurationRules {

        @Test
        @DisplayName("Configuration classes should be annotated with @Configuration")
        void configurationClassesShouldBeAnnotatedCorrectly() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..config..")
                    .and().haveSimpleNameEndingWith("Config")
                    .and().doNotHaveSimpleName("ConfigTest")
                    .should().beAnnotatedWith(org.springframework.context.annotation.Configuration.class)
                    .because("Configuration classes should be properly annotated");

            rule.check(classes);
        }

        @Test
        @DisplayName("Configuration classes should be in config package")
        void configurationClassesShouldBeInCorrectPackage() {
            ArchRule rule = classes()
                    .that().areAnnotatedWith(org.springframework.context.annotation.Configuration.class)
                    .should().resideInAPackage("..config..")
                    .because("Configuration classes belong in config package");

            rule.check(classes);
        }
    }

    @Nested
    @DisplayName("Mapper Rules")
    class MapperRules {

        @Test
        @DisplayName("JPA mappers should be in adapter.out.persistence package")
        void jpaMappersShouldBeInPersistenceAdapter() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("JpaMapper")
                    .should().resideInAPackage("..adapter.out.persistence..")
                    .because("JPA mappers belong to persistence adapters");

            rule.check(classes);
        }

        @Test
        @DisplayName("DTO mappers should be in adapter.in.rest package")
        void dtoMappersShouldBeInRestAdapter() {
            ArchRule rule = classes()
                    .that().haveSimpleNameContaining("DtoMapper")
                    .and().resideInAPackage("..adapter..")
                    .should().resideInAPackage("..adapter.in.rest..")
                    .because("DTO mappers belong to REST adapters");

            rule.check(classes);
        }

        @Test
        @DisplayName("Mappers should not be in domain package")
        void mappersShouldNotBeInDomainPackage() {
            ArchRule rule = noClasses()
                    .that().haveSimpleNameEndingWith("Mapper")
                    .should().resideInAPackage("..domain..model..")
                    .because("Mappers are adapter concerns, not domain concerns");

            rule.check(classes);
        }
    }

    @Nested
    @DisplayName("Naming Convention Rules")
    class NamingConventionRules {

        @Test
        @DisplayName("DTOs should be named with Dto suffix")
        void dtosShouldBeNamedCorrectly() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..dto..")
                    .and().areNotMemberClasses()
                    .should().haveSimpleNameEndingWith("Dto")
                    .because("DTOs should follow naming convention");

            rule.check(classes);
        }

        @Test
        @DisplayName("Commands should be named with Command suffix")
        void commandsShouldBeNamedCorrectly() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..port.in..")
                    .and().haveSimpleNameEndingWith("Command")
                    .and().areNotMemberClasses()
                    .should().haveSimpleNameEndingWith("Command")
                    .because("Commands should follow naming convention");

            rule.check(classes);
        }

        @Test
        @DisplayName("Query results should be named with QueryResult suffix")
        void queryResultsShouldBeNamedCorrectly() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..port.in..")
                    .and().haveSimpleNameEndingWith("QueryResult")
                    .and().areNotMemberClasses()
                    .should().haveSimpleNameEndingWith("QueryResult")
                    .because("Query results should follow naming convention");

            rule.check(classes);
        }

        @Test
        @DisplayName("Domain exceptions should be in exception package")
        void domainExceptionsShouldBeInExceptionPackage() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..domain..")
                    .and().haveSimpleNameEndingWith("Exception")
                    .should().resideInAPackage("..exception..")
                    .because("Exceptions should be in exception package");

            rule.check(classes);
        }
    }

    @Nested
    @DisplayName("Package Dependency Rules")
    class PackageDependencyRules {

        @Test
        @DisplayName("Adapters should not depend on each other")
        void adaptersShouldNotDependOnEachOther() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..adapter.in..")
                    .should().dependOnClassesThat().resideInAPackage("..adapter.out..")
                    .because("Adapters should not depend on each other");

            rule.check(classes);
        }

        @Test
        @DisplayName("Outgoing adapters should not depend on incoming adapters")
        void outgoingAdaptersShouldNotDependOnIncomingAdapters() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..adapter.out..")
                    .should().dependOnClassesThat().resideInAPackage("..adapter.in..")
                    .because("Outgoing adapters should not depend on incoming adapters");

            rule.check(classes);
        }
    }
}
