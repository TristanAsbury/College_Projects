myStack SEGMENT STACK
    DW 256 dup (?)
myStack ENDS

myData SEGMENT
myData ENDS

myCode SEGMENT

    main PROC
    assume ds: myData, cs: myCode
    
    mov ax, 0b800h
    mov es, ax

    mov di, 0       ; di contains address of first char
    mov si, 158     ; si contains address of last char
    mov cx, 24      ; ch has a counter for the rows
    push cx         ; for the first iteration, we must push the cx register onto the stack

    rowLoop:
        pop cx          ; pop the value of the row counter
        cmp cx, 0       ; are we on the last row?
        je done         ; if so, we are done
        dec cx          ; if not, then dec
        push cx si di   ; push all of our needed registers on the stack
        mov cx, 40      ; set cx to the column counter
        jmp colLoop     ; switch lines

    colLoop:
        cmp cx, 0           ; are we on the last character to switch?
        je nextLine         ; if so, go to next line
 
        mov ax, es:[di]     ; store the character at screen offset di in ax 
        mov bx, es:[si]     ; store the character at screen offset si in bx 

        mov es:[di], bx     ; place the character in bx in screen offset at di
        mov es:[si], ax     ; place the character in ax in screen offset at si

        add di, 2           ; add 2 to di, moving the left position to the next character
        sub si, 2           ; sub 2 from si, moving the right position to the previous character

        loop colLoop

    nextLine:
        pop di si           ; pop values of si and di from the stack
        add di, 160         ; add 160 to both, going to the next line
        add si, 160         ; 
        jmp rowLoop         ; go back to the rowLoop label
    
    done:
        call finish

    main ENDP

    finish PROC
        mov cx, 80*25*2     ; number of characters on screen
        mov si, 0

        top:
            cmp cx, 0           ; are we on the last character
            je exit             ; if so, exit
            mov ax, es:[si]

            cmp al, 'A'
            jl dontColor
            cmp al, 'Z'
            jle colorIt
            cmp al, 'a'
            jl dontColor
            cmp al, 'z'
            jle colorIt

        colorIt:
            mov ah, 01110001b   ; change the color of the character
            mov es:[si], ax     ; move the chracter to screen memory
            dec cx              ; decrement counter
            add si, 2           
            jmp top

        dontColor:
            mov ah, 00000111b   ; change the color of the character 
            mov es:[si], ax     ; move the character to screen memory
            add si, 2           ; decrement counter
            dec cx
            jmp top

        exit:
            mov AH, 4Ch     ; These two instructions use a DOS interrupt
            int 21h  
    finish ENDP
myCode ENDS
END main
