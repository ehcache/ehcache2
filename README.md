# Ehcache 2.x (community / FOSS edition) has reached End of Life.   

Ehcache 3.0 was released April 2016.

As of October 2022, this project will receive reduced attention, excepting for important/critical fixes.

After September 2023, we will no longer maintain Ehcache 2.x.


# To compile:

  %> mvn install -DskipTests
  
Note: the final Ehcache jar is found under ehcache/target  


# To build Ehcache distribution kit:

  %> cd distribution
  
  %> mvn package (build without an embedded Terracotta kit, lean and mean Ehcache kit)
  
  %> mvn package -Dtc-kit-url=http://url/to/teracotta.tar.gz  (built with Terracotta kit, offical distribution kit)
  
  
# To deploy Maven central repo (via Sonatype):

  %> mvn clean deploy -P sign-artifacts,deploy-sonatype -DskipTests
  
