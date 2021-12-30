# Overview

Repository where I am implementing [Crafting Interpreters by Robert Nystrom](https://www.amazon.ca/Crafting-Interpreters-Robert-Nystrom/dp/0990582930/ref=sr_1_1?keywords=crafting+interpreters&qid=1640893872&sprefix=crafting+in%2Caps%2C146&sr=8-1)

Crafting interpreters is a hands-on book about implementing interpreters for programing languages. 
It presents `Lox` a compact JavaScript like 'scripting' languages.

## Lox grammar

### Expressions

```
expression -> equality;
equality -> comparison ( ( "!=" | "==" ) comparison )* ;
comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term -> factor ( ("+" | "-") factor )* ;
factor -> unary ( ("*" | "/") unary )* ;
unary -> ( "!" | "-" ) unary | primary ;
primary -> NUMBER | SRING | "true" | "false" | "nil" | "(" expression ")" ;

```