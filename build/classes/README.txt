Custom Memory Manager
This project includes 3 classes
 CMM.java This is the main memory manager. It contains an CMMinFile and a CMMinMem. It uses the 2 of them as per requirement.
 
 CMMinMem This is a memory manager that uses a fixed size chunk of bytes as its memory(for example 4M). We keep allocating or deallocating objects from this space hence we prevent the use of garbage collector.
 
 CMMinFile This is a memory manager that uses a files as a backing store for objects.


The operations:
persist(objectName, object) this tries to save the data in the in memory system, if enough space is not available it uses the in file system.

fetch(objectName): this function will fetch the object from one of the underlying systems or return a null if the object is not present.

evict(objectName): this function will evict the object from the underlying systems.

Performance of the system for number of objects:

100 in memory system is good enough to handle all the objects there it will be a very fast system.
10000 a mix of in memory as well as in file systems will be used
1 billion objects for objects of this scope a database system is a better choice rather than a simple file system or in memory system.

Some additional improvements that could be done time permitting, to the existing system

the in memory system could be used as a cache rather than the simple implementation which I have provided. Using a simple eviction strategy like LRU, we could make some major performance improvements for workloads where certain objects are accessed more frequently compared to others.

To run the project use:

#javac custommemorymanager/CMM.java custommemorymanager/CMMinFile.java custommemorymanager/CMMinMemory.java

#java custommemorymanager/CMM
