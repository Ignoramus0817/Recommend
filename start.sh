${SPARK_HOME}/bin/spark-submit \
--master local[2] \
--class AR.Main \
--executor-memory 2G \
--driver-memory 2G \
/home/ignora/OS/Recommend/target/Question1-1.0-SNAPSHOT.jar \
"/home/ignora/OS/dataset" \
"/home/ignora/OS/output" \
"/home/ignora/OS/tmp"
