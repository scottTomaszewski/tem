TEM
===

Repo for Technical Exchange Meetings that I give.

* Dataflow: "The Ins and Outs of IO" Files, strings, byte arrays, InputStreams, etc are terrible.  Let's do better.  We will go over java's IO capabilities for handling data from files, strings, and byte arrays to InputStreams and why they all fall short.  Then, we will  check out guava's IO utilities and take them to the next level for fast, easy streaming workflows.

Since this isn't in maven central yet, execute the following to setup the TEM

    git clone git@github.com:KokaLabs/koka-corporate.git
    mvn clean install -f koka-corporate/pom.xml -T 1C
    
    git clone git@github.com:KokaLabs/koka-util.git
    mvn clean install -f koka-util/pom.xml  -T 1C
    
    git clone git@github.com:scottTomaszewski/tem.git
    mvn clean install -f tem/pom.xml -T 1C
