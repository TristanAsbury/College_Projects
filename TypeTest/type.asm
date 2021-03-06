; AUTHOR: TRISTAN ASBURY
; STRESS: HIGH
; FEELING AFTER GETTING IT DONE: EUPHORIC
; STACK SEGMENT

myStack SEGMENT STACK

    DW 256 DUP(?)

myStack ENDS

; DATA SEGMENT

myData SEGMENT

    first DB "Hello, I am just a sad little computer.", 0
    second DB "What a lovely day this is.", 0
    third DB "Ten little bugs crawled across the floor.", 0
    fourth DB "Climb at a high rate and you may stall!", 0
    fifth DB "Assembly is simply something that takes time to learn.", 0
    sixth DB "The more you use assembly, the more you turn into a computer.", 0
    seventh DB "I think all of my friends are turning into procedures.", 0
    eighth DB "Amino acids are essential to prevent muscle catabolism.", 0
    ninth DB "I need to use my arduino more! LEDs are fun.", 0
    tenth DB "Raising the major 3rd by a semitone creates a sus4 chord.", 0
    
    correctFlag DB 0        ; keeps track if the sentence is correct
    insertFlag DB 1         ; insert mode is enabled by default
    typedLength DW 0        ; how many chars the user typed
    isPlaying DB 0          ; is the user playing?
    cursorPos DW 0          ; holds the current offset of the cursor
    sentenceLength DW 0     ; the num of chars in the original sentence
    randomSentence DW 0     ; holds the address of the random sentence

    currentTime DW 0        ; the amount of tenths of seconds
    startTime DW 0          ; the starting time

    maxTime equ 1           ; time limit (in minutes)

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

    push 0              ; call showTime with argument being 0 tenths (because game hasn't started until player types)
    call showTime   

topCheck:
    call updateTimer    ; update timer
    call checkForShifts ; check for the shift keys
    call updateCursor   ; update cursor

    mov ah, 11h
    int 16h     ; peek if there is a key in the buffer
    jz topCheck ; if the user didn't type anything, dont handle any key
    mov ah, 10h 
    int 16h     ; get the key in the buffer and store in AL
    cmp al, 27  ; see if the user pressed esc
    je exit     ; if so, exit
    call processKey ; if its another key, process it
    call colorSentence  ; after the key has been processed, color the current sentence on the screen
    cmp correctFlag, 1  ; see if the sentence is correct
    je exit             ; if it is, exit
    jmp topCheck        ; go back to the top

exit:
    mov AH, 4Ch     ; These two instructions use a DOS interrupt to give control back to OS
    int 21h
main ENDP
;=========================================

;=========================================
startTimer PROC
    push cx ax dx
    mov ah, 00h
    int 1ah             ; get current ticks into dx
    mov startTime, dx   ; move dx into startTime
    pop dx ax cx
    ret
startTimer ENDP
;=========================================

;=========================================
updateTimer PROC
    push ax bx cx
    cmp isPlaying, 1        ; is the user currently in the game?
    jne exitUpdateTimer     ; if not, then don't update the timer

    push startTime          ; else, push the startTime
    call getTimeLapseTenths ; returns, in ax, the tenths of seconds since start time
    push ax                 ; push those tenths of seconds onto stack as an argument for showing time
    call showTime           ; call show time

    mov bx, ax              ; make bx hold the millis

    mov ax, 600             ; make ax 600 millis
    mov cx, maxTime         ; make cx the number of minutes (1 by default)
    mul cx                  ; multiply

    cmp bx, ax              ; compare the current time to the max time
    je exit                 ; if current time is equal to max tenths, then exit the program
    
exitUpdateTimer:
    pop cx bx ax
    ret
updateTimer ENDP
;=========================================

;=========================================
getTimeLapseTenths PROC
    push bp         ; push bp on stack to preserve
    mov bp, sp      ; make bp point to sp (which is currently pointing to preserved bp)
    push dx bx      ; preserve bx and dx

    mov ah, 00h     ; call the interrupt to get current ticks
    int 1ah

    sub dx, [bp+4]  ; subtract startTime from current time to get elapsed time of game
    mov ax, dx      ; move that time into ax
    mov bx, 55      ; multiply by 55 to get total amount of milliseconds
    mul bx          

    mov bx, 100     ; divide by 100 to get tenths of the seconds
    div bx
    
    pop bx dx bp
    ret 2           ; return to move stack pointer back to return address
getTimeLapseTenths ENDP
;=========================================

;=========================================
showTime PROC   
    push bp         ; preserve bp
    mov bp, sp      ; make bp point to sp (which is pointing to preserved bp)
    push ax bx dx si; preserve all other registers
    mov ax, [bp+4]  ; move into ax, the amount of tenths
    mov bx, 10      ; move 10 into bx
    mov si, 0       ; make si 0

pushDigitTop:
    cmp si, 4       ; see if we have reached the last digit
    je nextStep     ; if so, go to the next step

    mov dx, 0       ; if not, make dx 0
    div bx          ; divide ax by 10
    add dx, '0'     ; add '0' to dx to get the character
    push dx         ; push the character to the stack
    inc si          ; go to next position
    jmp pushDigitTop;jmp to top

nextStep:
    mov dx, 0       ; do final step
    div bx      
    add dx, '0'
    mov es:[160*19], dl ; put the character into that position
    mov si, 2           ; next position

topShowLoop:
    cmp si, 12      ; are we at the last printing position
    je exitShow     ; if so, exit
    cmp si, 8       ; are we at the position for the decimal
    je putDecimal   ; if so, do the decimal
    pop dx          ; else, pop the value at the current position in the timer
    mov es:[160*19+si], dl  ;print the value on the screen position
    add si, 2       ; next screen position
    jmp topShowLoop ; back to top

putDecimal:
    mov es:[160*19+si], '.'
    mov es:[160*19+si+1], 00000111b
    add si, 2
    jmp topShowLoop

exitShow:
    pop si dx bx ax bp
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
    push si di bx cx dx
    
    mov ax, 1  ; setting return value to true at the beginning of the check (innocent until proven guilty)

    mov si, [bp+4]  ; random sentence address
    mov di, [bp+6]  ; typed sentence address in ES
    mov bx, [bp+8]  ; typed length
    mov cx, 0

    cmp bx, sentenceLength  ; are the sentences the same length?
    jne checkLengths        ; if not, then set the flag to false already

topMatchSentence:
    cmp cx, sentenceLength ; is cx the typed length
    je exitMatchSentence
    mov dx, es:[di]
    cmp ds:[si], dl ; is the character right?
    jne isWrong     ; if its not the right character go to end 
    add di, 2       ; else, go to next written character on screen
    inc si          ; go to next character of sentence
    inc cx          ; increment cx (counter)
    jmp topMatchSentence    ; go back to top

isWrong:
    mov ax, 0
    jmp exitMatchSentence

checkLengths:
    mov ax, 0
    jmp topMatchSentence

exitMatchSentence:
    mov [bp+6], di  ; move the invalid character position to the sentence address variable we pushed onto the stack
    pop dx cx bx di si bp
    ret
sentencesMatch ENDP
;=========================================

;=========================================
colorSentence PROC
    push bp sp ax di
    push typedLength    ; number of chars user typed (2)
    push word ptr 21*160; address of ES where user typed sentence (2)
    push randomSentence ; offset in DS of original sentence (2)
    call sentencesMatch ; ax contains true of false

    mov correctFlag, al
    mov bp, sp          ; make bp point to the same thing sp is
    mov di, [bp+2]      ; DI contains the position of the incorrect letter
    add sp, 6           ; 'clean' the stack

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
    call getRandomNumber    
    mov si, randomSentence  ; we are looking at the first sentence
    mov di, 20 * 160    ; choose writing position on screen
    mov sentenceLength, 0
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

    mov ax, dx  ; AH contains remainder, put that into dx
    mov ah, 0   ; put 0 into ah
    mov bl, 10  ; move 10 into bl
    div bl      ; divide ax by 10
    
    lea si, first - 1   ; get our address of the first sentence, minus 1

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

    cmp ah, 3bh
    je handleF1

    cmp al, 0e0h
    je handleAuxiliary

    call doRegularKey   ; if its none of the important keys, handle it
    jmp bottom

handleF1:
    call restartGame
    jmp bottom

handleBackspace:
    call doBackspace
    jmp bottom

handleAuxiliary:
    call doAuxiliary
    jmp bottom

bottom:
    pop ax
    ret
processKey ENDP
;=========================================

;=========================================
restartGame PROC
    call writeSentence
    mov cursorPos, 0    ; mov cursor to beginning
    mov typedLength, 0  ; make typed length 0
    mov isPlaying, 0    ; the player hasn't started typing
    push 0
    call showTime           ; call show time
    ret
restartGame ENDP
;=========================================

;=========================================
checkForShifts PROC
    push ax

    mov ah, 12h ; call shift interrupt, stores bitfield in al
    int 16h

    mov cx, 160
    mov si, 21*160

    and al, 00000011b           ; check for shifts
    cmp al, 00000011b           
    je topClearSentence         ; if the user pressed both shifts, then restart
    jmp exitWithoutRestart      ; else, exit without restarting

topClearSentence:
    cmp cx, 0           ; are we at the end of the typing line?
    je exitWithRestart  ; if so, we are done
    mov es:[si], ' '    ; if not, then put a blank on the current position
    sub cx, 2           ; sub cx
    add si, 2           ; go to next character position
    jmp topClearSentence

exitWithRestart:
    mov cursorPos, 0    ; puts cursor at beginning
    mov typedLength, 0  ; makes typed length 0
    mov isPlaying, 0
    push 0
    call showTime           ; call show time

exitWithoutRestart:
    pop ax
    ret
checkForShifts ENDP
;=========================================

;=========================================
doAuxiliary PROC
    push ax
    cmp ah, 4bh         ; left arrow?
    je goLeft

    cmp ah, 4dh         ; right arrow?
    je goRight          

    cmp ah, 53h         ; delete key?
    je handleDelete

    cmp ah, 52h         ; insert key?
    je handleInsertPress

goLeft:
    cmp cursorPos, 0    ; are we at the beginning?
    je doneArrow        ; if we are, cant move anymore
    sub cursorPos, 2    ; if not, then move to the left
    jmp doneArrow       

goRight:
    mov ax, typedLength
    mov bl, 2
    mul bl
    cmp cursorPos, ax   ; are we are at the end?
    je doneArrow        ; if so, then we cant move anymore
    add cursorPos, 2    ; if not, then move to the right
    jmp doneArrow       ; go to end

handleDelete:
    call doDelete
    jmp doneArrow

handleInsertPress:
    mov al, insertFlag
    xor al, 00000001b
    mov insertFlag, al

doneArrow:
    pop ax
    ret
doAuxiliary ENDP
;=========================================

;=========================================
updateCursor PROC
    push ax 
    mov dh, 21          ; row
    mov ax, cursorPos   ; column
    mov bl, 2           ; divide by 2
    div bl          
    mov dl, al

    mov ah, 02h     
    int 10h         ; call the interrupt

    pop ax
    ret
updateCursor ENDP
;=========================================

;=========================================
doRegularKey PROC
    push ax si di

    cmp isPlaying, 1    ; has the game started?
    jne startGame        ; tell game to start
    jmp regularHandle

startGame:
    mov isPlaying, 1
    call startTimer

regularHandle:
    ;TODO: CHECK IF ITS LONGER THAN THE SCREEN LENGTH
    mov ah, 00000111b   ; put the color
    mov di, 21*160      ; set destination to the line
    add di, cursorPos   ; add cursor offset
    
    cmp typedLength, 160; did we type max amount of chars?
    je bottomRegKey     ; if so, we don't type anything

    cmp insertFlag, 1   ; is insert mode enabled?
    je handleInsert     ; if so, then use handleInsert
    jmp handleOverride ; else, use handleOverride

handleOverride:
    push ax             
    mov ax, typedLength
    mov bl, 2
    mul bl

    cmp cursorPos, ax       ; is the overriding character after the typed length???
    jge incrementTypedLength    ; if so, then we increment typed length
    jmp regularReplace      ; else, we just replace

incrementTypedLength:
    inc typedLength

regularReplace:
    pop ax
    cmp cursorPos, 160  ; are we at the end?
    je bottomRegKey     ; dont add it
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
    push si di bx ax
    
    mov ax, typedLength
    mov bl, 2
    mul bl

    cmp cursorPos, ax   ; are we past the end of our sentence?
    jge exitDel         ; if so, don't do anything

    cmp cursorPos, 160    ; is the cursor pos at the end
    je bottomDel        ; if so, dont do anything

    dec typedLength     ; decrement the typed length
    mov si, 21*160      ; move into si, the line of our typed sentence
    add si, cursorPos   ; add the cursorpos offset
    add si, 2           ; add 2

    mov di, 21*160      ; get the character at the current position
    add di, cursorPos   ; add the character offset

    call shiftTailLeft  ; shift the tail left

exitDel:
    pop ax bx di si
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
    je bottomDel            ; if so, leave

    mov bx, es:[si]         ; if not, store, into bx, the character at si
    mov es:[di], bx         ; put that character into position di

    add si, 2               ; increment both
    add di, 2
    
    jmp topDelLoop          ; back to top

bottomDel:
    mov bx, 32              ; move a space to bx
    mov es:[di], bx         ; move that to the last position

    inc di                  ; increment di
    mov bx, 00001111b       ; make it white
    mov es:[di], bx         ; move that color into the last position
    inc di                  ; increment

pop bx di si
ret
shiftTailLeft ENDP
;=========================================

myCode ENDS

END main 
