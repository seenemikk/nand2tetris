// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.


    // Set current color to white
    @0
    D = A
    @current_color
    M = D

(LOOP)
    @SCREEN
    D = A
    @addr
    M = D

    @KBD
    D = M  // D = KBD
    
    @SET_WHITE  // D == 0; JMP SET_WHITE
    D; JEQ
    @SET_BLACK  // ELSE: JMP SET_BLACK
    0; JMP

(SET_BLACK)
    // Set current color to black
    @current_color
    M = -1
    
    @SCREEN_DRAW
    0; JMP

(SET_WHITE)
    // Set current color to white
    @current_color
    M = 0
    
    @SCREEN_DRAW
    0; JMP
    

(SCREEN_DRAW)
    @addr
    D = M
    @KBD
    D = D - A
    @LOOP
    D; JEQ

    @current_color
    D = M
    @addr
    A = M
    M = D
    
    @addr
    M = M + 1
    
    @SCREEN_DRAW
    0; JMP
    
