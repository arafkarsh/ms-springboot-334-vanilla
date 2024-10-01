# JMeter v5.5 

- Ensure that JMeter is installed and its available in the PATH
- To Run the Jmeter test case run the following command.

```
jmeter -n -t ms-sb-272-j8.jmx -l ms-sb-272-j8-2023-06-20-13-27-20.jtl -e -o jmoutput-2023-06-20-13-27-20
```

Here's a description of the options used:

- -n: This specifies JMeter is to run in non-GUI mode
- -t: The name of the JMX file that contains the Test Plan
- -l: The name of the file to which the test log is written
- -e: Generate an HTML report at end of the test and save it to the specified directory (-o option).
- -o: Path to the directory where the HTML report will be generated.

## Execute the Test case

When you run the command (shell script) "run" you will get the following output

![JMeter RUN](https://raw.githubusercontent.com/arafkarsh/ms-springboot-272-java-8/master/jmeter/diagrams/Jmeter-Run.jpg)

## JMeter Output

![JMeter RUN](https://raw.githubusercontent.com/arafkarsh/ms-springboot-272-java-8/master/jmeter/diagrams/Jmeter-Result.png)


