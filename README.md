# Final Project. 

A program that uses a CLI based interface for users to define object schemas based on domain java classes that can determine field subsets of the original class. From these validated object schemas, objects can be instantiated and related to other user-defined objects dynamically at runtime. The use of the Reflections Library is what allows the program to read in the base classes at runtime and obtain Class<?> references which enable the rest of the program to use the java.lang.reflect API. The program is useful as a way to test object relationships and create variations through runtime usage rather than code.

Program supports schema fields of type primitive/wrapper integer, double, boolean, String, singular object reference, ArrayList of object references of same object type. 
Domain base classes cannot be abstract or interface classes, have more than one constructor, or have a constructor that relies on schema-mutuable field values during construction. 

Features include creating schemas defined from base class field lists. Creating objects from user-made schemas which can be related to other user-made schema objects via singular references or collections (ArrayLists) of references. Displaying schemas and their associated objects. 

Example Use Case:
- Define schema: Student(name, courses)
- Create object: Alice
- Link Alice to multiple Course objects
- System builds and visualizes relationships dynamically
