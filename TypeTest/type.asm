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

    insertFlag DB 1         ; insert mode is enabled by default

    typedLength DW 0        ; how many chars the user typed

    sentenceLength DW 0     ; the num of chars in the original sentence
    randomSentence DW 0
    startTime DW 0

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
    call startTimer

topCheck:
    ; MAIN LOOP, LOOK FOR KEY INPUT
    call updateTimer
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
startTimer PROC
    push cx ax dx
    mov ah, 00h
    int 1ah

    mov startTime, dx
    
    pop dx ax cx
    ret

startTimer ENDP
;=========================================

;=========================================
updateTimer PROC
    push startTime
    call getTimeLapseTenths
    push ax
    call showTime
    ret
updateTimer ENDP
;=========================================

;=========================================
getTimeLapseTenths PROC
    push bp
    mov bp, sp
    push dx bx

    mov ah, 00h
    int 1ah

    sub dx, [bp+4]
    mov ax, dx
    mov bx, 55
    mul bx

    mov bx, 100
    div bx
    
    pop bx dx bp
    ret 2
getTimeLapseTenths ENDP
;=========================================

;=========================================
showTime PROC
    push bp
    mov bp, sp
    push ax bx dx

    mov ax, [bp+4]
    mov bx, 10

    mov dx, 0
    div bx
    add dx, '0'
    push dx

    mov dx, 0
    div bx
    add dx, '0'
    push dx
    
    mov dx, 0
    div bx
    add dx, '0'
    push dx

    mov dx, 0
    div bx
    add dx, '0'
    push dx

    mov dx, 0
    div bx
    add dx, '0'
    mov es:[160*2], dl
    mov es:[160*2+1], 00000111b

    pop dx
    mov es:[160*2+2], dl
    mov es:[160*2+3], 00000111b

    pop dx
    mov es:[160*2+4], dl
    mov es:[160*2+5], 00000111b

    pop dx
    mov es:[160*2+6], dl
    mov es:[160*2+7], 00000111b

    mov es:[160*2+8], '.'
    mov es:[160*2+9], 00000111b

    pop dx
    mov es:[160*2+10], dl
    mov es:[160*2+11], 00000111b

    pop dx bx ax bp
    ret 2

showTime ENDP
;=========================================

;=========================================
sentencesMatch PROC
    ; ON ENTER: 3 things on stack
    ; char* requiredSentence (pointer to the required sentence) (WORD)
    ; char* userTypedSentence (pointer to screen memory) (WORD)
    ; int numCharsTyped         (typedLength)   (WORD)
    ; return address
    ; old BP <- SP and BP
    
    push bp     ; keep old bp
    mov bp, sp  ; bp is now pointing to itself
    push si di bx cx

    mov si, [bp+4]  ; random sentence address
    mov di, [bp+6]  ; typed sentence address in ES
    mov bx, [bp+8]  ; typed length
    mov cx, 0

topMatchSentence:
    cmp cx, ds:[bx] ; is cx the typed length
    je exitMatchSentence
    mov ax, es:[di]
    cmp ds:[si], al ; is the character right?
    jne isWrong     ; if its not the right character go to end 
    add di, 2       ; else, go to next written character on screen
    inc si          ; go to next character of sentence
    inc cx          ; increment cx (counter)
    jmp topMatchSentence    ; go back to top

isWrong:
    mov [bp+6], di  ; move the invalid character position to the sentence address variable we pushed onto the stack

exitMatchSentence:
    pop cx bx di si bp
    ret
sentencesMatch ENDP
;=========================================

;=========================================
colorSentence PROC
    push bp sp ax di
    push typedLength    ; number of chars user typed (2)
    push word ptr 21*160; address of ES where user typed sentence (2)
    push randomSentence ; offset in DS of original sentence (2)
    call sentencesMatch ; adds return address to the stack

    mov bp, sp          ; make bp point to the same thing sp is
    mov di, [bp+2]      ; DI contains the position of the incorrect letter
    add sp, 6           ; 'clean' the stack
    mov ax, es:[di]     ; move the incorrect letter (with color) into ax
    mov es:[320], ax    ; put ax onto screen

    mov si, 21*160

topColorLoop:
    cmp si, 22*160      ; are we at end?
    jge exitColoring    ; if so, exit
    cmp si, di          ; compare the character positions (SI is current DI is error character)
    jl colorCorrect
    jmp colorIncorrect
    add si, 2
    jmp topColorLoop

colorCorrect:
    inc si
    mov es:[si], byte ptr 00001111b
    inc si
    jmp topColorLoop    

colorIncorrect:
    inc si
    mov es:[si], byte ptr 00001100b
    inc si
    jmp topColorLoop

exitColoring:
    pop di ax sp bp
    ret
colorSentence ENDP
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
    inc sentenceLength
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
    mov ah, 00000111b   ; put the color
    mov di, 21*160      ; set destination to the line
    add di, cursorPos   ; add cursor offset

    cmp typedLength, 160; did we type max amount of chars?
    je bottomRegKey     ; if so, we don't type anything
    
    cmp insertFlag, 1   ; is insert mode enabled?
    je handleInsert
    jmp handleOverwrite

handleOverwrite:
    cmp cursorPos, 160  ; are we at the end?
    je bottomRegKey     ; dont add it
    inc typedLength
    mov es:[di], ax     ; else, put character on screen
    add cursorPos, 2
    jmp bottomRegKey

handleInsert:
    cmp cursorPos, 160  ; are we at the end?
    je bottomRegKey     ; dont add it

    cmp typedLength, 80
    je bottomRegKey

    inc typedLength
    call shiftTailRight
    mov es:[di], ax     ; else, put character on screen
    add cursorPos, 2
    jmp bottomRegKey

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

    dec typedLength
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
    
    cmp cursorPos, 160    ; is the cursor pos at the end
    je bottomDel    

    dec typedLength
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

    cmp typedLength, 160    ;is the sentence length the max length?
    je exitShiftRight       ;leave the proc if so

    mov si, 21*160+156          ; make the end char
    mov di, 21*160+158          ; make the char before that
    mov cx, 160

topShiftRight:
    cmp cx, cursorPos
    je exitShiftRight
    mov bx, es:[si] ; move the character to the left into bx
    mov es:[di], bx ; move the character in bx onto screen space to the right
    sub si, 2       ; go to the next characters (to the left)
    sub di, 2
    sub cx, 2
    jmp topShiftRight

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
