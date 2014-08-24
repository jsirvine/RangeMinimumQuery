Range Minimum Query Structures
jirvine (Jamie Irvine)

Description
-----------
This repo contains four range minimum query structures, with a common RMQ
interface (RMQ.java). It was built for the CS166 course at Stanford. The repo 
also contains an test driver (RMQDriver.java) which runs tests against a "gold
standard" rmq structure, found as the class file oursolution.class

RMQ Structures
--------------
Each structure has a precomputation step and a query step, and the time complexities
are expressed in terms of &lt;O(precompute), O(query)&gt;.

The first structure is a precomputed RMQ structure (PrecomputedRMQ.java). This
structure dynamically precomputes all possible ranges. It has complexity &lt;
O(n<sup>2</sup>), O(1)&gt;

The next structure precomputes a sparse table of RMQ solutions
(SparseTableRMQ.java). A query then finds the minimum between the two appropriate 
overlapping ranges that represent the range in question. The sparse table has
2<sup>k</sup> for each k, starting at each possible index of the array so there
are O(n log n) of them. It can be computed dynamically and in this way, the 
structure has time complexity &lt;O(n log n), O(1)&gt;.

The third structure is a hybrid RMQ structure (HybridRMQ.java). It uses a
two-layered approach. The bottom layer is the original array. For the top layer,
the array is divided into equal sized blocks and each minimum is computed. The minimum
over a range is the min of each block fully contained and the array indices on
either end of the range, in partially contained blocks. By setting the block size to
be log n, and constructing a sparse table RMQ strucure over the top blocks, the
overall complexity is &lt;O(n), O(log n)&gt;.

The final structure is the Fischer-Heun RMQ structure, which is amazingly 
&lt;O(n), O(1)&gt;. It also uses a two layered approach. It reduces redundancy
by generalizing similarly structured RMQ ranges. Each block on the top layer can
be mapped to a unique Cartesian tree, which can be represented as a Cartesian
number. Two structures with the same Cartesian number also have the same index
solutions to any range minimum query. By only storing one precomputed RMQ 
structure for each cartesian number and making block size log<sub>2</sub>/4, we
ensure the complexities stated above.

RMQ Driver
----------
The RMQ test driver was written by the professor, with a gold standard rmq
solution class file for reference. It can be run with the following command

  java -ea RMQDriver rmq.&lt;your-rmq-class&gt; [random-seed]
