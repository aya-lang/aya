# sample.aya

A sample aya package that defines angle conversion utilities

## Installation

  1. Place this package in the `pkg` directory of your aya installation.
  2. Import the package with `import sample`

## Package Structure

  - `__pkg__.aya`: The entrypoint for the package. This file should bring package variables into scope. It should generally only have import/require statements in it
  - `package.json`: Package metadata
    - `name`: The package name
    - `version`: Version is `major.minor.patch` format
    - `author`: Package author
  - `src/`: Location of aya source files
  - `test/`: Location of aya test files. All aya files in this folder will be ran when using `pkg.test`



