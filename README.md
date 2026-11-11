# ph-ee-identity-account-mapper

All PRs should be raised against Dev. Dev is the latest branch of pre-release code Dev->Main will be undertaken through a release cycle by Admins.

All issues are managed in Jira https://mifosforge.jira.com/jira/software/c/projects/PHEE/issues?jql=project%20%3D%20PHEE%20ORDER%20BY%20created%20DESC

All PRs must have a Jira Issue reference or will be rejected

All Contributors must have signed a Mifos Individual Contributor Agreement and agree to the Mifos Code of Conduct and Contributor Guidelines

The following Branch Names and Tags (and derivatives and extensions e.g. releasev1.0) are reserved for use by Mifos Organisation any branches created by non-admins with these names will be deleted without notice:

main master dev development sec security mifos release rel rc staging prod production

## Spotless
Use below command to execute the spotless apply.
```shell
./gradlew spotlessApply
```

## Checkstyle
Use below command to execute the checkstyle test.
```shell
./gradlew checkstyleMain
```
