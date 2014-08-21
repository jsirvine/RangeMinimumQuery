CLASSFILES = RMQDriver.class driver/RMQFactory.class rmq/PrecomputedRMQ.class rmq/SparseTableRMQ.class rmq/HybridRMQ.class rmq/FischerHeunRMQ.class

all: $(CLASSFILES)

RMQDriver.class: RMQDriver.java
	javac $<

driver/RMQFactory.class: driver/RMQFactory.java
	javac $<

rmq/PrecomputedRMQ.class: rmq/PrecomputedRMQ.java
	javac $<

rmq/SparseTableRMQ.class: rmq/SparseTableRMQ.java
	javac $<

rmq/HybridRMQ.class: rmq/HybridRMQ.java
	javac $<

rmq/FischerHeunRMQ.class: rmq/FischerHeunRMQ.java
	javac $<

clean:
	rm -f *~ *.class
	rm -f rmq/*~ rmq/*.class
	rm -f driver/*~ driver/*.class
