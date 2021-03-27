// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// Put your code here.

    // a = R0
    @R0
    D = M
    @a
    M = D
     
    // b = R1
    @R1
    D = M
    @b
    M = D
    
    // c = 0
    @0
    D = A
    @R2
    M = D
    
    // i = 0
    @0
    D = A
    @i
    M = D  

(LOOP)
    @i
    D = M
    @b
    D = D - M
    @END
    D; JEQ
    
    // c += a
    @a
    D = M
    @R2
    M = M + D
    
    //i++
    @i
    M = M + 1
    
    @LOOP
    0; JMP

(END)
    @END
    0; JMP
