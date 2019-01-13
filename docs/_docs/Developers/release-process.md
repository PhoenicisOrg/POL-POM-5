---
title: "Release process"
category: Developers
order: 8
toc: false
---

The following steps must be executed to release a new version of Phoenicis:
* Create release branches (e.g. "release/5.0-beta") for phoenicis and scripts
* Using the scheme "release/\*" automatically protects the release branch with:
    * Require pull request reviews before merging
    * Require status checks to pass before merging (Travis CI and Codacy)
* Enable analysis for release branches (phoenicis and scripts) in Codacy settings
* on the release branch:
    * Add release branch to `branches` section of `.travis.yml` such that Travis CI executes checks for the branch
    * Specify scripts release branch in configuration (`application.repository.default.git.branch`)
    * Set release version for Maven in `pom.xml` files
    * Updated `<releases>` in `phoenicis-dist/src/flatpak/org.phoenicis.playonlinux.appdata.xml` (also on master)
* [Test]({{ site.baseurl }}{% link _docs/Developers/test-plan.md %})
* Create GitHub release from the release branches for phoenicis and scripts
    * attach .tar.gz (build with `phoenicis-dist/src/scripts/phoenicis-create-package.sh`)
    * attach .deb (build with `phoenicis-dist/src/scripts/phoenicis-create-package.sh`)
    * attach .dmg
    * attach flatpak .zip
    * attach .flatpak (see [single-file bundle](http://docs.flatpak.org/en/latest/single-file-bundles.html))
* Announce release on phoenicis.org
    * Showcase new features
    * List major changes/fixed bugs
