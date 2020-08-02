When you have Maven's project that uses parent-pom with defaults for your convenience you are temped to add
dependencies from the parent or from the [BOM](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#importing-dependencies).

For example when you define you company wide parent and want to use Spring Boot as main dependency you have two
options how to create your own parent pom:
- inherit your **parent from Spring Boot's** `spring-boot-starter-parent`
- create your parent as vanilla parent and add **Spring Boot's BOM** called `spring-boot-dependencies`

**Main purpose of this project is** to show how you have to deal with this two types of ways how to create parent pom, when
you want to use automatic dependency resolution with future version update.

# Parent from Spring Boot parent

There is `parent-from-boot` module that contains your own parent pom inherited directly from `spring-boot-starter-parent`.
Spring Boot's parent pom defines dependencies and also default plugins that helps to build your Boot application.

In module `sample-for-parent-from-boot` (which inherits from your custom `parent-from-boot` module) is shown that
you can easily override dependency version by modification of property from `spring-boot-dependencies` module.

In this case we have `commons-lang3` version `3.9` defined in current Spring Boot version `2.2.3.RELEASE`.
Module `sample-for-parent-from-boot` overrides property `commons-lang3.version` with value `3.11` and test
`ParentLang3Test` asserts correct Lang3 dependency version on class-path.

# Parent with Spring Boot BOM

You can create your own parent pom and add dependencies using BOM import. It is normal use-case, but when you 
want to build Spring Boot application with this custom parent pom, you have to be aware of following "problems":

- some default properties have to be copy-pasted
- `resources` configuration have to be copy-pasted
- build plugin `spring-boot-maven-plugin` have to be configured
- `pluginManagement` with default configuration of plugins has to be copy-pasted
  - `kotlin-maven-plugin`
  - `maven-compiler-plugin`
  - `maven-failsafe-plugin`
  - `maven-jar-plugin`
  - `maven-war-plugin`
  - `maven-resources-plugin`
  - `git-commit-id-plugin`
  - `spring-boot-maven-plugin`
  - `maven-shade-plugin`
Also, bear in mind that when Spring Boot updates build process you would have to update this copy-pasted defaults too.
When you copy-paste and modify this default, it is highly recommended documenting changes you made and why you made them.

## Override Spring Boot dependency version

Modules `parent-boot-bom` and `sample-for-parent-boot-bom` show how to correctly override version of one dependency
from imported BOM of `spring-boot-dependencies`.

You can see there that naive approach modifying `commons-lang3.version` property does not work.
One can suppose that because property `commons-lang3.version` is defined in BOM `spring-boot-dependencies` dependency,
it can be overridden by modifying it in owh pom. But this is not how Maven works when importing dependencies from BOM.

To show correct way how to allow versions modification you have to see `my-commons-lang3.version` property.
There are three steps you have to follow in your custom parent pom to allow versions override:
1. define `my-commons-lang3.version` property with value copy-pasted from Spring Boot's BOM
2. redefine `commons-lang3` in `dependencyManagement` with `version` element with placeholder to your property: `${my-commons-lang3.version}`
3. in the module that want to override version set value of property `my-commons-lang3.version` and add dependency to `commons-lang3`

See `BomLang3Test` for assertions.

_Note: if you don't do modifications in your custom parent pom you would not be allowed to override versions this way.
See `parent-boot-bom-invalid` module for details._

## Parent pom without explicit dependency to customize

As mentioned above when you create own parent pom without dependency with explicitly defined version property, you 
cannot easily modify `version` in you child poms. 

Module `parent-boot-bom-invalid` is similar to `parent-boot-bom` module, but without `commons-lang3` dependency
with customizable property.

Module `sample-for-parent-boot-bom-invalid` that even when you specify property `commons-lang3.version` and 
dependency to `commons-lang3` you will always get same version `3.9`. Test `BomInvalidLang3Test` asserts this 
invalid state.

## Hard to maintain version updates

Last module `parent-boot-bom-not-updated` show that it is hard to maintain version updates from imported BOM.

Use case is that you have own parent-pom, and you have added `dependencyManagement` to dependency you want to allow
version customization - as mentioned "Override Spring Boot dependency version".

In past you have imported Spring Boot version `2.2.3.RELEASE` and created `commons-lang3` version `3.9` with custom 
property to allow version customization. Later you have updated Spring Boot to version `2.3.1.RELEASE` that
imports `commons-lang3` version `3.10` as dependency. In you projects module you have dependency on `commons-lang3`
without version property specified. What you expect is that when updating Spring Boot version, also `commons-lang3`
will be updated. 

Unfortunately because you have created own version property and set it to "old" value `3.9`, it downgrades the version
from new Spring Boot. I don't have to mention that this is not expected behavior.

_Note: when you allow customization this way with BOM you have to be prepared to manually update default versions
from overridden properties._

Test `BomNotUpdatedLang3Test` asserts this invalid state.

# Conclusion

From details described above it is highly recommended creating you parent-pom as child from Spring Boot's parent-pom.