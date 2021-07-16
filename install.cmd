:: https://stackoverflow.com/a/4955695

mvn install:install-file ^
   -Dfile=target/ExcelUtil-1.0.jar ^
   -DgroupId=io.nambm ^
   -DartifactId=ExcelUtil ^
   -Dversion=1.0 ^
   -Dpackaging=jar ^
   -DgeneratePom=true