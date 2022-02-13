myStack SEGMENT STACK

    DW 256 dup (?)

myStack ENDS

;==========================

myData SEGMENT

myData ENDS

;===========================

myCode SEGMENT

    main PROC
    assume ds: myData, cs: myCode
    
    mov ax, 0b800h
    mov es, ax

    mov di, 0       ; di contains address of first char
    mov si, 158     ; si contains address of last char
    mov ch, 24      ; ch has a counter for the rows

    rowLoop:
        mov cl, 40      ; cl has a counter for the cols
        cmp ch, 0       ; are we on the last row?
        je done         ; if so, we are done
        dec ch          ; if not, then dec
        jmp colLoop     ; switch lines

    colLoop:
        cmp cl, 0           ; are we on the last character to switch?
        je nextLine         ; if so, go to next line
 
        ; store characters
        mov ax, es:[di]     ; store the character at screen offset di in ax 
        mov bx, es:[si]     ; store the character at screen offset si in bx 

        ; switch characters
        mov es:[di], bx     ; place the character in bx in screen offset at di
        mov es:[si], ax     ; place the character in ax in screen offset at si

        ; go to next position
        add di, 2           ; add 2 to di, moving the left position to the next character
        sub si, 2           ; sub 2 from si, moving the right position to the previous character

        dec cl              ; dec the column counter
        jmp colLoop         ; repeat

    nextLine:
        sub di, 80          ; subtract 80 from di, resetting its position to the left
        add si, 80          ; add 80 to si, resetting its position to the right
        add di, 160         ; add 160 to both, going to the next line
        add si, 160         ; 
        jmp rowLoop         ; go back to the rowLoop header
    
    done:
        mov AH, 4Ch     ; These two instructions use a DOS interrupt
        int 21h         

    main ENDP
myCode ENDS
END main
