# Artifact for the Paper "Do not neglect what's on your hands: localizing software faults with exception trigger stream"

This is the artifact for the ASE 2024 research paper "***Do not neglect what's on your hands: localizing software faults with exception trigger stream***". This artifact supplies the replication package (the code and the instruction for replicating our experiments) and the supplementary material (relevant data of the motivating example and exploratory study) of the paper.

## Requiring

maven >= 3.8.1

JDK 17

python 3.9

JaCoCo 0.8.7

# Fault localization

## Attaining Pseudo Correct Versions

As mentioned in our paper, pseudo correct versions are recommended to be attained using history versions or different development branches of the program under test. As an alternative solution, generating mutants of the program is acceptable and the tool could be built refer to [Arun-Babu/mutate.py: A simple script to perform mutation testing on c/c++ like programs (github.com)](https://github.com/arun-babu/mutate.py).

To conveniently run the test suite against a program version and collect the result of each test case, add the following plugins to the pom.xml file in the program under test, then run **[SurefireReportReader.java](replication/tools/SurefireReportReader.java)** to get the report file.

```Java
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-failsafe-plugin</artifactId>
  <version>2.22.2  </version>
</plugin>
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>2.22.2</version>
  <configuration>
    <testFailureIgnore>true</testFailureIgnore>
    <redirectTestOutputToFile>true</redirectTestOutputToFile>
    <reportsDirectory>target/surefire-reports</reportsDirectory>
    <systemProperties>
      <property>
        <name>surefire.testng.verbose</name>
        <value>10</value>
      </property>
    </systemProperties>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter-engine</artifactId>
      <version>5.5.0</version>
    </dependency>
  </dependencies>
</plugin>
```

## Bifurcation Point Tracing + Voting

Add the following dependency and plugin to the pom.xml file in the program under test, use **[CollectCoverage.java](replication/tools/CollectCoverage.java)** to extract coverage information gathered by JaCoCo when running a test case, and make sure to extract coverage information of at least one test case, such information is needed for exception trigger stream-related source code instrumentation.

```Java
<dependency>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.7</version>
</dependency>
```

```Java
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.7</version>
  <executions>
    <execution>
      <goals>
        <goal>prepare-agent</goal>
      </goals>
    </execution>
    <execution>
      <id>report</id>
      <phase>test</phase>
      <goals>
        <goal>report</goal>
      </goals>
      <configuration>
        <outputDirectory>target/jacoco-report</outputDirectory>
      </configuration>
    </execution>
  </executions>
</plugin>
```

Use **[insertprint.java](replication/EXPECT/insertprint.java)** and **[insertexcept.java](replication/EXPECT/insertexcept.java)** to complete the source code instrumentation. Remember to modify the path of the coverage file in these two files to the path of the coverage file collected by **[CollectCoverage.java](replication/tools/CollectCoverage.java)**. Modification to the instrumentation logic or human intervention may be needed for specific programs.

Run **[compare.py](replication/tools/compare.py)** to generate commands for collecting exception trigger streams of failed test cases, execute these commands on both the faulty program and the corresponding pseudo correct versions (all these versions need source code instrumentation). When executing these commands, **[collect.java](replication/EXPECT/collect.java)** needs to be compiled.

Run **[ebfl_vote.py](replication/EXPECT/ebfl_vote.py)** to trace the bifurcation points between exception trigger streams collected in passed and failed executions, a vote-base ranking will be generated.

## Tie-breaking

Run any suspiciousness evaluation method to get a suspiciousness ranking for breaking the tie, you can simply introduce an SBFL formula and calculate a suspiciousness score for each program statement based on the coverage information extracted by **[CollectCoverage.java](replication/tools/CollectCoverage.java)**.

Use **[analysis.py](replication/EXPECT/analysis.py)** to print the final suspiciousness ranking.

# Motivation Example

**[motivationExample&runningExample](motivationExample&runningExample)** gives the example program in Section 3 of our paper, as well as the entire exception trigger streams collected in passed and failed executions and the vote-based ranking.

# Running Example

**[motivationExample&runningExample](motivationExample&runningExample)** gives the relevant information of running EXPECT on the example program in Section 3.

# Exploratory Study

**[exploratoryStudy](exploratoryStudy)** gives the faulty versions of our exploratory study, each version contains the following data:

**[injected_mutation](exploratoryStudy/version_1/injected_mutation)**: The source code file that contains a fault compared with the original Gson program.

**[exception_trigger_stream_original](exploratoryStudy/version_1/exception_trigger_stream_original)**: Exception trigger streams of failed test cases collected in the original Gson program.

**[exception_trigger_stream_faulty](exploratoryStudy/version_1/exception_trigger_stream_faulty)**: Exception trigger streams of failed test cases collected in the faulty program.

**[exception_trigger_stream_pseudo](exploratoryStudy/version_1/exception_trigger_stream_pseudo)**: Exception trigger streams of failed test cases collected in the pseudo correct versions. For each failed test case, we check the faulty versions after the current version one by one and use the first faulty version that turns the execution result of the failed test case to correct as the pseudo correct version.

By comparing **[exception_trigger_stream_original](exploratoryStudy/version_1/exception_trigger_stream_original)** and **[exception_trigger_stream_faulty](exploratoryStudy/version_1/exception_trigger_stream_faulty)**, it can be verified that whether the faulty statement lies close to the bifurcation point in each faulty version, the results are shown below.

| Faulty versions | The faulty statement lies close to the bifurcation point |
| :--------: | :------------------------------------------------------------: |
| version_1  | TRUE |
| version_2  | TRUE |
| version_3  | TRUE |
| version_4  | TRUE |
| version_5  | TRUE |
| version_6  | TRUE |
| version_7  | TRUE |
| version_8  | TRUE |
| version_9  | TRUE |
| version_10  | TRUE |
| version_11  | TRUE |
| version_12  | TRUE |
| version_13  | TRUE |
| version_14  | TRUE |
| version_15  | TRUE |
| version_16  | TRUE |
| version_17  | TRUE |
| version_18  | TRUE |
| version_19  | TRUE |
| version_20  | TRUE |
| version_21  | TRUE |
| version_22  | TRUE |
| version_23  | TRUE |
| version_24  | TRUE |
| version_25  | TRUE |
| version_26  | TRUE |
| version_27  | TRUE |
| version_28  | TRUE |
| version_29  | TRUE |
| version_30  | TRUE |
| version_31  | TRUE |
| version_32  | TRUE |
| version_33  | FALSE |
| version_34  | FALSE |
| version_35  | FALSE |
| version_36  | FALSE |
| version_37  | FALSE |
| version_38  | FALSE |
| version_39  | FALSE |
| version_40  | FALSE |

By comparing **[exception_trigger_stream_original](exploratoryStudy/version_1/exception_trigger_stream_original)** and **[exception_trigger_stream_pseudo](exploratoryStudy/version_1/exception_trigger_stream_pseudo)**, it can be verified that whether the exception trigger information in exception trigger streams collected in the original program and the pseudo correct versions is the same in each faulty version, the results are shown below.

| Faulty versions | The number of failed test cases | The number of consistent pairs of exception trigger streams |
| :--------: | :------------------------------------------------------------: | :------------------------------------------------------------: |
| version_1  | 4 | 4 |
| version_2  | 3 | 3 |
| version_3  | 5 | 5 |
| version_4  | 2 | 2 |
| version_5  | 1 | 1 |
| version_6  | 2 | 2 |
| version_7  | 2 | 2 |
| version_8  | 1 | 1 |
| version_9  | 3 | 3 |
| version_10  | 1 | 1 |
| version_11  | 1 | 1 |
| version_12  | 1 | 1 |
| version_13  | 2 | 2 |
| version_14  | 1 | 1 |
| version_15  | 1 | 1 |
| version_16  | 4 | 4 |
| version_17  | 3 | 3 |
| version_18  | 1 | 1 |
| version_19  | 2 | 1 |
| version_20  | 1 | 1 |
| version_21  | 4 | 3 |
| version_22  | 2 | 2 |
| version_23  | 2 | 2 |
| version_24  | 2 | 2 |
| version_25  | 3 | 2 |
| version_26  | 3 | 3 |
| version_27  | 3 | 3 |
| version_28  | 2 | 2 |
| version_29  | 3 | 3 |
| version_30  | 3 | 3 |
| version_31  | 2 | 2 |
| version_32  | 3 | 3 |
| version_33  | 3 | 2 |
| version_34  | 3 | 3 |
| version_35  | 2 | 2 |
| version_36  | 3 | 3 |
| version_37  | 2 | 2 |
| version_38  | 1 | 1 |
| version_39  | 2 | 2 |
| version_40  | 3 | 3 |
