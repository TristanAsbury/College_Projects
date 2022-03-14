; STACK SEGMENT

myStack SEGMENT STACK

    DW 256 DUP(?)

myStack ENDS

; DATA SEGMENT

myData SEGMENT

    first DB "this is torture", 0
    second DB "i am going insane", 0
    third DB "i am in a constant state of suffering", 0
    fourth DB "assembly is a new torturing method", 0
    fifth DB "the void will consume us all.", 0
    sixth DB "this is saddening", 0
    seventh DB "all my friends are turning into procedures", 0
    eighth DB "this is wacky", 0
    ninth DB "sad sad sad", 0
    tenth DB "weeeee", 0

    cursorPos DW 0
    correctFlag DB 0        ; keeps track if the sentence is correct
    insertFlag DB 0

    typedLength DW 0        ; how many chars the user typed
    sentenceLength DW 0     ; the num of chars in the original sentence
    randomSentence DW 0

myData ENDS

; CODE SEGMENT
myCode SEGMENT
assume ds: myData, cs: myCode

;==========MAIN PROC======================
main PROC
	mov ax, myData      ; Make DS point to data segment
	mov ds, ax

    mov ax, 0b800h  
    mov es, ax          ; move start of screen memory address into es

    call writeSentence

topCheck:
    ; MAIN LOOP, LOOK FOR KEY INPUT
    call getTimeLapseTenths ; this will store the timelapse tenths in bx
    mov ah, 11h
    int 16h
    jz topCheck ; if the user didn't type anything, dont handle any key
    mov ah, 10h
    int 16h
    cmp al, 27  ; see if the user pressed esc
    je exit
    call processKey
    call colorSentence
    
    jmp topCheck

exit:
    mov AH, 4Ch     ; These two instructions use a DOS interrupt
    int 21h
main ENDP
;=========================================

;=========================================
getTimeLapseTenths PROC
    push ax bx cx dx

    mov ah, 00h
    int 1ah

    mov si, 160*5+80

    ; cx:dx = number of timer ticks since midnight
    mov ax, dx  ; dx contains low order of ticks
    mov ah, 0

    mov bl, 10
    div bl

    add al, 30h

    mov es:[si], al

exitShowTime:
    pop dx cx bx ax
    ret
getTimeLapseTenths ENDP
;=========================================

;=========================================
sentencesMatch PROC
    ; ON ENTER: 3 things on stack
    ; char* requiredSentence (pointer to the required sentence) (WORD)
    ; char* userTypedSentence (pointer to screen memory) (BYTE)
    ; int numCharsTyped         (typedLength)   (WORD)
    
    push bp     ; keep old bp
    mov bp, sp  ; bp is now pointing to itself

    mov si, word ptr 5*160
    mov es:[si], byte ptr 'e'

    pop bp
    ret
sentencesMatch ENDP
;=========================================

;=========================================
colorSentence PROC

    push typedLength    ; number of chars user typed (2)
    push word ptr 21*160; address of ES where user typed sentence (2)
    push randomSentence ; offset in DS of original sentence (2)
    call sentencesMatch
    add sp, 6           ; 'clean' the stack


colorSentence ENDP
;=========================================


;=========================================
; colorSentence PROC
;     push ax bx cx si di
;     mov correctFlag, byte ptr 1
;     mov di, 160*20  ;regular sentence
;     mov si, 160*21  ;typed sentence
;     mov cx, sentenceLength

; topColor:
;     cmp cx, 0           ; are we at the last character to compare?
;     je exitColor        ; if so, exit
;     cmp correctFlag, 1  ; if the users typing is correct
;     jne colorIncorrect  ; if the flag is false, don't compare and just color the next characters wrong
;     mov bx, es:[di]     ; else, make bx the reg char
;     mov ax, es:[si]     ;       make ax the typed char
;     cmp al, bl          ; compare the characters (low)
;     jne colorIncorrect  ; if they arent equal, then make incorrect
;     jmp colorCorrect    ; if they are, color correct

; colorCorrect:
;     mov ah, 00001010b   ; make it green if its correct
;     mov es:[si], ax
;     add si, 2
;     add di, 2      
;     dec cx
;     jmp topColor

; colorIncorrect:
;     mov correctFlag, 0
;     mov ax, es:[si]
;     mov ah, 00001100b   ; make it red if its incorrect
;     mov es:[si], ax
;     add si, 2
;     add di, 2
;     dec cx
;     jmp topColor

; exitColor:
;     pop di si cx bx ax
;     ret
; colorSentence ENDP
;=========================================

;=========================================
writeSentence PROC
    push ax si di
    
    call clearScreen
    call getRandomNumber; 
    mov si, randomSentence       ; we are looking at the first sentence
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

;=========================================
clearScreen PROC
    push cx si
    mov si, 0
    mov cx, 2000

topClear:
    cmp cx, 0
    je exitClear
    mov es:[si], byte ptr ' '
    inc si
    mov es:[si], byte ptr 00000111b
    inc si
    dec cx
    jmp topClear

exitClear:
    pop si cx
    ret
clearScreen ENDP
;=========================================

;=========================================
getRandomNumber PROC
    push ax bx dx si
    
    mov ah, 00h
    int 1ah

    mov ax, dx  ; AH contains remainder
    mov ah, 0
    mov bl, 10
    div bl
    
    lea si, first - 1

    cmp ah, 0
    je exitFindSentence

topFindSentence:
    inc si                  ; increment character position
    cmp ds:[si], byte ptr 0 ; have we found a terminator?
    jne topFindSentence     ; if not, then go to next char
    inc bh                  ; if we have, then increment found sentence
    cmp bh, ah              ; if bh is ah
    je exitFindSentence     ; if they are the same, we found the sentence
    jmp topFindSentence     

exitFindSentence:
    inc si
    mov randomSentence, si
    pop si dx bx ax
    ret
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
    mov ah, 00000111b   ; make the character green
    mov di, 21*160      ; set destination to the line
    add di, cursorPos   ; add cursor offset
    cmp cursorPos, 160  ; are we at the end?
    je bottomRegKey     ; dont add it
    inc sentenceLength
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
shiftTailRight PROC

push si di bx

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
    pop bx di si
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

myCode ENDS

END main 
