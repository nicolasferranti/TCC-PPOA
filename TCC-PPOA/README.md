Dependências que não estão no Maven:
$ mvn deploy:deploy-file -Dfile=/home/nicolasferranti/Documentos/TCC-Nicolas/TCC-PPOA/lib/align.jar -DgroupId=org.semanticweb -DartifactId=owl.align -Dversion=1.0 -Dpackaging=jar -Durl=file:./maven-repository/ -DrepositoryId=maven-repository -DupdateReleaseInfo=true

$ mvn deploy:deploy-file -Dfile=/home/nicolasferranti/Documentos/TCC-Nicolas/TCC-PPOA/lib/api.jar -DgroupId=org.semanticweb -DartifactId=owl.model -Dversion=1.0 -Dpackaging=jar -Durl=file:./maven-repository/ -DrepositoryId=maven-repository -DupdateReleaseInfo=true

$ mvn deploy:deploy-file -Dfile=/home/nicolasferranti/Documentos/TCC-Nicolas/TCC-PPOA/lib/procalign.jar -DgroupId=fr.inrialpes.exmo -DartifactId=align.parser -Dversion=1.0 -Dpackaging=jar -Durl=file:./maven-repository/ -DrepositoryId=maven-repository -DupdateReleaseInfo=true

