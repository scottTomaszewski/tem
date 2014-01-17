tem
===

Since this isn't in maven central yet, execute the following to setup the TEM

git clone git@github.com:KokaLabs/koka-corporate.git
mvn clean install -f koka-corporate/pom.xml -T 1C

git clone git@github.com:KokaLabs/koka-util.git
mvn clean install -f koka-util/pom.xml  -T 1C

git clone git@github.com:scottTomaszewski/tem.git
mvn clean install -f tem/pom.xml -T 1C
