myStack SEGMENT STACK

    DW 256 dup (?)

myStack ENDS

myData SEGMENT

myData ENDS

myCode SEGMENT
    assume ds: myData, cs: myCode

    main PROC
    push ax es di si cx ; add our register values to stack
        mov ax, 0b800h  ; move the screen memory address to ax
        mov es, ax      ; move ax (containing screen memory address) to es 

        mov di, 0       ; di contains address of first char
        mov si, 158     ; si contains address of last char
        mov cx, 25      ; cx has a counter for the rows

        rowLoop:
            call switch
            loop rowLoop

        call color
        pop cx si di es ax  ; pop register values from stack
        mov AH, 4Ch     ; These two instructions use a DOS interrupt
        int 21h             
        ret
    main ENDP

    switch PROC 
        push ax bx cx si di     ; add our register values to the stack
        mov cx, 40              ; set the counter to 40 (how many cols there are each side)

        top:
            mov ax, es:[di]     ; store the character at screen offset di in ax 
            mov bx, es:[si]     ; store the character at screen offset si in bx 
            mov es:[di], bx     ; place the character in bx in screen offset at di
            mov es:[si], ax     ; place the character in ax in screen offset at si
            add di, 2           ; add 2 to di, moving the left position to the next character
            sub si, 2           ; sub 2 from si, moving the right position to the previous character
            loop top

        pop di si cx bx ax      ; pop register values from stack
        add si, 160             ; set the char pointers to next line
        add di, 160
        ret                     ; return
    switch ENDP

    color PROC
        push ax bx cx si di     ; add our registers to the stack
        mov cx, 25*80           ; set the counter to 4000
        mov si, 0               ; set the char pointer to first position

        topColor:
            cmp cx, 0           ; are we on the last character?
            je exit             ; if so, jump to exit
            mov ax, es:[si]     ; if not, then move the character into ax
            cmp al, 'A'         ; test to see if its a valid character
            jl dontColor        ; if not, dont color it
            cmp al, 'Z'         
            jle doColor         ; if it is, do color it
            cmp al, 'a'
            jl dontColor        ; if not, dont
            cmp al, 'z'
            jle doColor         ; if it is, do

        doColor:
            mov ah, 01110001b   ; set the background to white with blue colored text
            mov es:[si], ax     ; move the character color back into screen memory
            add si, 2           ; move the char pointer to next char
            dec cx              ; decrement the counter
            jmp topColor        ; go back to top
        
        dontColor:
            mov ah, 00000111b   ; set the background to black with grey colored text
            mov es:[si], ax     ; move the characer color back into screen memory
            add si, 2           ; move the char pointer to next char
            dec cx              ; decrement the counter
            jmp topColor        ; go back to top

        exit:
        pop di si cx bx ax      ; pop register values from stack
        ret                     ; return
    color ENDP
myCode ENDS
END main
