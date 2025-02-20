# Bhai Chara Programming Language - README

## Introduction  
The **Bhai Chara Programming Language** is a custom programming language designed with simplicity and readability in mind, using familiar keywords in a **conversational, friendly style**. It supports fundamental programming constructs such as **arithmetic operations, variables, conditional logic, loops, and input/output functions**.

The syntax is straightforward and follows strict rules for variable naming, ensuring a structured and easy-to-understand coding experience.

---

## Features  
✔ **Arithmetic Operators**: Supports standard operators (`+`, `-`, `*`, `/`, `%`, `=`).  
✔ **Strict Variable Naming Rules**:
   - Variables must start with a **capital letter (A-Z)**.  
   - Variables can only contain **letters (A-Z) and digits (0-9)**.  
   - No spaces allowed in variable names.  
✔ **Comments**:
   - Single-line: `??` → (e.g., `?? this is a comment`)  
   - Multi-line: `<< ... >>` → (e.g., `<< multiple line comment this is be happy or sad about it >>`)  
✔ **Input/Output Functions**:
   - `inputdo` → Take input from the user  
   - `dekhana` → Print output to the screen  
✔ **Data Types**:
   - `bbool` → Boolean  
   - `bint` → Integer  
   - `bfloat` → Float  
   - `bchar` → Character  
   - `jamaIlfaz` → String  
✔ **Conditional Statements**:
   - `bhaiagar` → `if`  
   - `agarwarna` → `else if`  
   - `bhaiwarna` → `else`  
✔ **Functions**:
   - `chaleye` → Function definition  

---

## Language Syntax  

### 1. Variables  
```plaintext
bint X = 5;
bfloat Pi = 3.14;
jamaIlfaz Naam = "zubair";
bbool Haq = true;
```

### 2. Arithmetic Operations  
```plaintext
bint Jama = 5 + 10;
bfloat Zarb = 2.5 * 4;
```

### 3. Conditional Statements  
```plaintext
bhaiagar (X > 3) {
    dekhana("x is great!");
} agarwarna (X == 3) {
    dekhana("x is equal!");
} bhaiwarna {
    dekhana("x is small!");
}
```

### 4. Loops  
```plaintext
tillWhenBro (X < 10) {
    dekhana("X value: " + X);
    X = X + 5;
}
```

### 5. Input/Output  
```plaintext
jamaIlfaz Naam;
inputdo(Naam);
dekhana("Hello, " + Naam);
```

### 6. Comments  
```plaintext
? ? Ye aik single-line comment hai

<<
la
la
la
la
la
aaaaaaaaa
>>
```

---

## Example Program  
```plaintext
?? simple program

bint X;
inputdo(X);
dekhana("uer input is x = ", X);
bhaiagar (X > 5) {
    dekhana("X is greater than 5");
} agarwarna (X == 5) {
    dekhana("X is equal 5");
} bhaiwarna {
    dekhana("X is smaller than 5");
}
```

---

## How It Works  
1️⃣ **Lexical Analysis**: The lexer tokenizes the input code, identifying keywords, variables, and operators.  
2️⃣ **Parsing**: The parser ensures the syntax follows the predefined rules and structures.  
3️⃣ **Execution**: The interpreter executes the parsed instructions, handling control structures, mathematical computations, and user interactions.  

---

## Future Enhancements  
✅ Support for **functions and parameters**  
✅ Implementation of **arrays and lists**  
✅ Enhanced **error handling and debugging tools**  
✅ Expansion of **built-in libraries** for utility operations  

---

## Conclusion  
The **Bhai Chara Programming Language** makes coding **fun, intuitive, and conversational**

If you have **ideas, contributions, or improvements**, feel free to extend this project!  

**Happy Coding! 🚀**
