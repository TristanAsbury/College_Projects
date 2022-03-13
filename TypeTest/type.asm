; STACK SEGMENT

myStack SEGMENT STACK

    DW 256 DUP(?)

myStack ENDS

; DATA SEGMENT

myData SEGMENT

    first DB "this is torture", 0
    second DB 'another thing', 0

    cursorPos DW 0



myData ENDS

; CODE SEGMENT

myCode SEGMENT

assume ds: myData, cs: myCode

;==========MAIN PROC======================
main PROC
	mov ax, MyData ; Make DS point to data segments
	mov ds, ax 

    mov ax, 0b800h  
    mov es, ax          ; move start of screen memory address into es

    call writeSentence

topCheck:
    ; MAIN LOOP, LOOK FOR KEY INPUT
    mov ah, 11h
    int 16h
    jz topCheck ; if the user didn't type anything, dont handle any key
    mov ah, 10h
    int 16h
    cmp al, 27  ; see if the user pressed esc
    je exit
    call processKey
    jmp topCheck

exit:
    mov AH, 4Ch     ; These two instructions use a DOS interrupt
    int 21h

main ENDP
;=========================================

;=========================================
processKey PROC
    push ax
    
    cmp al, 8           ; check if backspace
    je handleBackspace

    cmp al, 127         ; check if delete
    je handleDelete

    call doRegularKey   ; if its none of the important keys, handle it
    jmp bottom

handleBackspace:
    call doBackspace
    jmp bottom

handleDelete:
    call doDelete
    jmp bottom

bottom:
    pop ax
    ret

processKey ENDP
;=========================================

;=========================================
doRegularKey PROC
    push ax si di
    
    ;TODO: CHECK IF ITS LONGER THAN THE SCREEN LENGTH

    mov ah, 00001010b   ; make the character green
    mov di, 21*160      ; set destination to the line
    add di, cursorPos   ; add cursor offset
    cmp cursorPos, 160  ; are we at the end?
    je bottomRegKey     ; dont add it

    mov es:[di], ax     ; else, put character on screen

    add cursorPos, 2

bottomRegKey:
    pop di si ax
    ret

doRegularKey ENDP
;=========================================

;=========================================
doBackspace PROC
    push si di bx
    
    cmp cursorPos, 0    ; is the cursor pos at the beginning
    je bottomBackspace

    sub cursorPos, 2    ; move cursorPos back 1 character
    mov si, 21*160
    add si, cursorPos
    mov di, 21*160      ; get the character at the current position
    add di, cursorPos
    sub di, 2           ; go to the character before that
    

topBSLoop:
    cmp di, 21 * 160 + 158  ; are we at the end?
    je bottomBackspace

    mov bx, es:[si]
    mov es:[di], bx

    add si, 2
    add di, 2
    
    jmp topBSLoop

bottomBackspace:
    
    pop bx di si
    ret

doBackspace ENDP
;=========================================

doDelete PROC
    push si di bx
    
    ;start at cursorpos - 2
    cmp cursorPos, 0    ; is the cursor pos at the beginning
    je bottomBackspace

    mov si, 21*160
    add si, cursorPos
    mov di, 21*160      ; get the character at the current position
    add di, cursorPos
    sub di, 2           ; go to the character before that
    
topDelLoop:
    cmp di, 21*160 + 158         ; are we at the end of the backspace loop?
    je bottomBackspace

    mov bx, es:[si]
    mov es:[di], bx

    add si, 2
    add di, 2
    jmp topBSLoop

bottomDel:
    pop bx di si
    ret
doDelete ENDP


; GET SENTENCE PROC
writeSentence PROC
    push ax si di
    
    
    
    lea si, first       ; we are looking at the first sentence

    mov di, 20 * 160    ; choose writing position on screen

topWriteLoop:
    mov al, ds:[si]     ; move current character into AL
    cmp al, 0           ; are we at the terminator (not the movie)
    je done             ; go to done label
    mov ah, 00001100b   ; give color to character in AH
    mov es:[di], ax     ; mov char
    add di, 2           ; next screen position
    inc si              ; next character
    jmp topWriteLoop

done:
    pop di si ax
    ret

writeSentence ENDP

myCode ENDS

END main 
