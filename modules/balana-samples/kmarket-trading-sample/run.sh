BALANA_CLASSPATH=""
for f in lib/*.jar
do
  BALANA_CLASSPATH=$BALANA_CLASSPATH:$f
done
for g in ../../balana-core/target/*jar
do
  BALANA_CLASSPATH=$BALANA_CLASSPATH:$g
done
for h in target/*jar
do
  BALANA_CLASSPATH=$BALANA_CLASSPATH:$h
done
BALANA_CLASSPATH=$BALANA_CLASSPATH:$CLASSPATH

$JAVA_HOME/bin/java -classpath "$BALANA_CLASSPATH" org.wso2.balana.samples.kmarket.trading.KMarketAccessControl $*



