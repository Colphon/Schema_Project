# AI Usage and Design Notes

AI tools (such as Copilot) were used throughout development as a supplement for exploring ideas, validating approaches, and accelerating learning. The system design, implementation decisions, and overall structure were determined and refined through iterative problem-solving.

---

## Initial System Design and Planning

Early in the project, AI was used to help outline the high-level steps required to build the system and to conceptualize the structure of a CLI-based interface.

This process helped clarify an important distinction:

- Complex logic (validation, type handling) is a coding problem  
- Object linking, instantiation flow, and schema behavior are system design problems  

From this, the core workflow was established:

- Define base classes  
- Create schemas from those classes  
- Instantiate and connect objects dynamically at runtime  

This framing led to the decision to use existing `Student` and `Section` classes as base models, allowing focus on schema behavior and object relationships rather than domain modeling.

AI was also used during this phase to:
- explore potential system extensions (e.g., persistence, CSV-driven input)
- assist in organizing package structure
- refine understanding of class relationships for UML design

---

## Environment Setup and Reflection Integration

During project initialization, AI assisted with:

- resolving Maven and language server synchronization issues
- debugging visibility problems related to project configuration
- learning the syntax and usage patterns of the Reflections library and `java.lang.reflect`

This helped establish a working development environment and allowed runtime class discovery to function correctly.

---

## Schema Definition and Type Handling

While implementing schema definition functionality, AI was used to:

- diagnose conflicts between Java modules and the Reflections library
- resolve issues involving language server caching and project reloads

A significant challenge during this stage was handling fields with generic types (e.g., `ArrayList<Section>`). AI assisted in understanding how to extract and process this information correctly using reflection.

This led to deeper understanding of:

- working with `ParameterizedType`
- distinguishing between raw types and generic arguments
- converting reflection results into usable display and validation information

AI also helped identify important constraints and edge cases, such as:

- excluding static fields  
- rejecting non-concrete generic types  
- limiting supported field types to maintain project scope and clarity  

---

## Object Instantiation and Validation

During object creation implementation, AI was used to:

- validate the overall approach for collecting, parsing, and validating user input
- refine the structure of the implementation for clarity and correctness
- review code for potential bugs and improvements

One key improvement during this stage was the use of `Map<>` to associate schema field names with validated values, allowing flexible handling of runtime input.

A major design constraint identified during this phase was:

- domain classes should only have a single constructor  
- constructors must not rely on schema-mutable field values  

To accommodate schema flexibility, object instantiation uses default constructor values, with schema-defined values applied after creation.

This ensures:
- compatibility with partial field definitions  
- predictable object construction behavior  

This design decision reflects a tradeoff between flexibility and strict enforcement of domain constraints.

AI also helped evaluate feature scope, leading to the removal of object deletion functionality, which was determined to add unnecessary complexity relative to project goals.

---

## Object Viewing and System Refinement

During the implementation of schema and object display functionality, AI assisted with:

- understanding and applying `Optional` for flexible method behavior
- designing a dedicated `ObjectViewer` component for structured output
- refining display logic to support schema-filtered views of base classes

After implementing core functionality, AI was used to:

- identify and fix multiple bugs across the system  
- suggest relevant unit tests (JUnit)  
- assist with generating and refining Javadoc  
- help align the UML diagram with the final system structure  

---

## Key Takeaway

AI served as a tool for:

- accelerating learning of unfamiliar concepts (reflection, generics)
- validating ideas during development
- identifying edge cases and constraints

However, all architectural decisions, tradeoffs, and implementations were iteratively developed and refined through independent reasoning and problem-solving.