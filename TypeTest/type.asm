; STACK SEGMENT

myStack SEGMENT STACK

    DW 256 DUP(?)

myStack ENDS

; DATA SEGMENT

myData SEGMENT

    first DB "this is torture", 0
    second DB 'another thing', 0

    cursorPos DW 0
    currectFlag DB 0        ;keeps track if the sentence is correct
    insertFlag DB 0
    
    typedLength DW 0        ;how many chars the user typed
    sentenceLength DW 0     ;the num of chars in the original sentence

    randomSentence DB 0

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
getRandomNumber PROC
push

mov ah, 00h
int 1ah

mov ax, dx
mov dx, 0

mov bx, 10
div bx

mov randomSentence, ah

pop
getRandomNumber ENDP
;=========================================

;=========================================
processKey PROC
    push ax
    
    cmp al, 8           ; check if backspace
    je handleBackspace

    cmp al, 0e0h
    je handleAuxiliary
    

    call doRegularKey   ; if its none of the important keys, handle it
    jmp bottom

handleBackspace:
    call doBackspace
    jmp bottom

handleAuxiliary:
    call doAuxiliary
    jmp bottom

bottom:
    call updateCursor
    pop ax
    ret

processKey ENDP
;=========================================

;=========================================
doAuxiliary PROC
    push ax
    cmp ah, 4bh
    je goLeft

    cmp ah, 4dh
    je goRight

    cmp ah, 53h
    je handleDelete

goLeft:
    cmp cursorPos, 0    ; are we at the beginning?
    je doneArrow
    sub cursorPos, 2
    jmp doneArrow

goRight:
    cmp cursorPos, 160
    je doneArrow
    add cursorPos, 2
    jmp doneArrow

handleDelete:
    call doDelete
    jmp doneArrow

doneArrow:
    pop ax
    ret
doAuxiliary ENDP
;=========================================

;=========================================
updateCursor PROC

    push ax
    mov dh, 21
    
    mov ax, cursorPos
    mov bl, 2
    div bl

    mov dl, al

    mov ah, 02h
    int 10h

    pop ax
    ret
updateCursor ENDP
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
    je exitBackspace

    sub cursorPos, 2    ; move cursorPos back 1 character
    mov si, 21*160
    add si, cursorPos
    add si, 2 
    mov di, 21*160      ; get the character at the current position
    add di, cursorPos

    call shiftTailLeft

exitBackspace:
    pop bx di si
    ret

doBackspace ENDP
;=========================================

;=========================================
shiftTailRight PROC

push 

    cmp sentenceLength, 160 ;is the sentence length the max length?
    je exitShiftRight       ;leave the proc if so

    mov si, 160*21
    add si, sentenceLength
    sub si, 2

    mov di, 160*21
    add di, sentenceLength

topShiftRight:
    cmp si, 160*21 + cursorPos  ; we are going right to left, so we want to see if the source index is at our cursor
    je exitShiftright
    mov bx, es:[si]
    mov es:[di], bx
    sub si, 2
    sub di, 2

exitShiftRight:
    pop
    ret
shiftTailRight ENDP
;=========================================

;=========================================
shiftTailLeft PROC
push si di bx

topDelLoop:
    cmp di, 21 * 160 + 158  ; are we at the end?
    je bottomDel

    mov bx, es:[si]
    mov es:[di], bx

    add si, 2
    add di, 2
    
    jmp topDelLoop

bottomDel:
    mov bx, 32
    mov es:[di], bx

    inc di
    mov bx, 00001111b
    mov es:[di], bx

    inc di

pop bx di si
ret
shiftTailLeft ENDP
;=========================================


;========================================
doDelete PROC
    push si di bx
    
    cmp cursorPos, 160    ; is the cursor pos at the beginning
    je bottomDel

    mov si, 21*160
    add si, cursorPos
    add si, 2

    mov di, 21*160      ; get the character at the current position
    add di, cursorPos

    call shiftTailLeft

exitDel:
    pop bx di si
    ret

doDelete ENDP
;=========================================

;=========================================
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
;=========================================

myCode ENDS

END main 
