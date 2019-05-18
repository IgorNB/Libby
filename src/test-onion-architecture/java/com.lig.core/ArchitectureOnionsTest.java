package com.lig.core;

import com.lig.libby.Main;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaPackage;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AnalyzeClasses(packagesOf = {Main.class})
public class ArchitectureOnionsTest {
    @ArchTest
    private static final ArchRule thereAreNoPackageCycles = SlicesRuleDefinition.slices()
            .matching(Util.BASE_PACKAGE + ".(**)..")
            .should()
            .beFreeOfCycles();
    @ArchTest
    private static final ArchRule theDomainDoesNotHaveOutgoingDependencies = ArchRuleDefinition.noClasses()
            .that()
            .resideInAPackage(Util.DOMAIN_PACKAGE + "..")
            .and()
            .haveSimpleNameNotEndingWith("Test")
            .should()
            .accessClassesThat()
            .resideInAnyPackage(Util.getAllPackageSubPackagesExcept(Util.BASE_PACKAGE, Arrays.asList(Util.DOMAIN_PACKAGE)));
    @ArchTest
    private static final ArchRule theRepositoryDependsOnlyOnDomain = ArchRuleDefinition.noClasses()
            .that()
            .resideInAPackage(Util.REPOSITORY_PACKAGE + "..")
            .and()
            .haveSimpleNameNotEndingWith("Test")
            .should()
            .accessClassesThat()
            .resideInAnyPackage(Util.getAllPackageSubPackagesExcept(Util.BASE_PACKAGE, Arrays.asList(Util.DOMAIN_PACKAGE, Util.REPOSITORY_PACKAGE)));
    @ArchTest
    private static final ArchRule theServiceDependsOnlyOnDomainAndRepository = ArchRuleDefinition.noClasses()
            .that()
            .resideInAPackage(Util.SERVICE_PACKAGE + "..")
            .and()
            .haveSimpleNameNotEndingWith("Test")
            .should()
            .accessClassesThat()
            .resideInAnyPackage(Util.getAllPackageSubPackagesExcept(Util.BASE_PACKAGE, Arrays.asList(Util.DOMAIN_PACKAGE, Util.REPOSITORY_PACKAGE, Util.SERVICE_PACKAGE)));
    @ArchTest
    private static final ArchRule theSecurityDependsOnlyOnDomainAndRepository = ArchRuleDefinition.noClasses()
            .that()
            .resideInAPackage(Util.SECURITY_PACKAGE + "..")
            .and()
            .haveSimpleNameNotEndingWith("Test")
            .should()
            .accessClassesThat()
            .resideInAnyPackage(Util.getAllPackageSubPackagesExcept(Util.BASE_PACKAGE, Arrays.asList(Util.DOMAIN_PACKAGE, Util.REPOSITORY_PACKAGE, Util.SECURITY_PACKAGE)));
    @ArchTest
    private static final ArchRule theApplicationDependsOnlyOnDomainAndRepositoryAndServiceAndSecurity = ArchRuleDefinition.noClasses()
            .that()
            .resideInAPackage(Util.APPLICATION_PACKAGE + "..")
            .and()
            .haveSimpleNameNotEndingWith("Test")
            .should()
            .accessClassesThat()
            .resideInAnyPackage(Util.getAllPackageSubPackagesExcept(Util.BASE_PACKAGE, Arrays.asList(Util.DOMAIN_PACKAGE, Util.REPOSITORY_PACKAGE, Util.SERVICE_PACKAGE, Util.SECURITY_PACKAGE, Util.APPLICATION_PACKAGE)));
    @ArchTest
    private static final ArchRule theControllerDependsOnlyOnDomainModelAndRepositoryAndServiceAndSecurityAndApplicatioin = ArchRuleDefinition.noClasses()
            .that()
            .resideInAPackage(Util.CONTROLLER_PACKAGE + "..")
            .and()
            .haveSimpleNameNotEndingWith("Test")
            .should()
            .accessClassesThat()
            .resideInAnyPackage(Util.getAllPackageSubPackagesExcept(Util.BASE_PACKAGE, Arrays.asList(Util.CONTROLLER_PACKAGE, Util.DOMAIN_PACKAGE, Util.REPOSITORY_PACKAGE, Util.SERVICE_PACKAGE, Util.SECURITY_PACKAGE)));

    @ParameterizedTest()
    @MethodSource("controllerChildPackagesProvider")
    public void theControllerAdaptersDoesNotDependOnEachOther(String adapterPackage) {
        ArchRuleDefinition.noClasses()
                .that()
                .resideInAPackage(adapterPackage + "..")
                .and()
                .haveSimpleNameNotEndingWith("Test")
                .should()
                .accessClassesThat()
                .resideInAnyPackage(Util.getAllPackageSubPackagesExcept(Util.CONTROLLER_ADAPTER_PACKAGE, Arrays.asList(adapterPackage)))
                .check(Util.CLASSES);

    }

    public List<String> controllerChildPackagesProvider() {
        return Util.getOnlyChildSubPackagesOf(Util.CONTROLLER_ADAPTER_PACKAGE);
    }

    private static class Util {
        private static String BASE_PACKAGE = Main.class.getPackage().getName();
        private static String DOMAIN_PACKAGE = BASE_PACKAGE + ".domain";
        private static String REPOSITORY_PACKAGE = Util.BASE_PACKAGE + ".repository";
        private static String SERVICE_PACKAGE = Util.BASE_PACKAGE + ".service";
        private static String SECURITY_PACKAGE = Util.BASE_PACKAGE + ".security";
        private static String CONTROLLER_PACKAGE = Util.BASE_PACKAGE + ".controller";
        private static String CONTROLLER_ADAPTER_PACKAGE = Util.CONTROLLER_PACKAGE + ".adapter";
        private static String APPLICATION_PACKAGE = BASE_PACKAGE + ".config";


        private static JavaClasses CLASSES = new ClassFileImporter().importPackages(BASE_PACKAGE);

        private static String[] getAllPackageSubPackagesExcept(String packageName, List<String> excludedPackageList) {
            return CLASSES
                    .stream()
                    .map(JavaClass::getPackage)
                    .filter(Objects::nonNull)
                    .map(JavaPackage::getName)
                    .distinct()
                    .filter(checkedPackage -> checkedPackage.startsWith(packageName) && !checkedPackage.equals(packageName))
                    .filter(checkedPackage -> excludedPackageList.stream().noneMatch(checkedPackage::startsWith))
                    .map(pn -> pn + "..")
                    .toArray(String[]::new);
        }

        private static List<String> getOnlyChildSubPackagesOf(String packageName) {
            return CLASSES
                    .stream()
                    .map(JavaClass::getPackage)
                    .filter(Objects::nonNull)
                    .map(JavaPackage::getName)
                    .distinct()
                    .filter(checkedPackage -> checkedPackage.startsWith(packageName + "."))
                    .filter(checkedPackage -> checkedPackage.lastIndexOf(".") == (packageName + ".").lastIndexOf("."))
                    .collect(Collectors.toList());
        }
    }
}
