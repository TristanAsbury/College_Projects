;=========SEGMENT============
myStack SEGMENT STACK
    DB 256 DUP (?)
myStack ENDS
;=========END OF SEGMENT=====

;=========SEGMENT============
myData SEGMENT
    randomLetters DB 3 DUP (?)  ; original letters
    copiedLetters DB 3 DUP (?)  ; letters copied over
    seed DW 1                   ; seed is initally 1
    targetWaitTime DW (?)       ; target wait time
    numCorrect DB 0             ; number of correct letters typed
    startTime DW (?)            ; start time used for the timer
myData ENDS
;=========END OF SEGMENT=====

;=========SEGMENT============
myCode SEGMENT
    assume ds: myData, cs: myCode

;============================
main PROC
    mov ax, 0b800h
    mov es, ax
    mov ax, myData
    mov ds, ax

    call clearScreen        ; clear the screen
    call pickRandomChars    ; then pick the random characters
    call copyChars          ; then copy the characters into the copiedLetters
    call waitTime           ; then wait a random amount of time
    call scatterChars       ; then scatter the chosen characters across the screen 
    call startTimer         ; start the timer

topMainLoop:
    call updateTimer        ; update the timer
    mov ah, 11h             ; look in the key buffer
    int 16h
    jz topMainLoop          ; if its empty, then jump to top
    mov ah, 10h             ; else, get the key
    int 16h                 ; ^^
    cmp al, 27              ; is it the esc key?
    je exitMain             ; if so, exit
    call handleKey          ; else, handle the character they typed
    jmp topMainLoop         ; go back to top

exitMain:
    call endGame            ; end the game

main ENDP
;============================

;============================
endGame PROC
    mov ah, 4ch
    int 21h
endGame ENDP
;============================

;============================
copyChars PROC
    push ax cx si di
    lea si, randomLetters   ; move address of randomLetters into si
    lea di, copiedLetters   ; move address of the copiedLetters into di
    mov cx, 0               ; cx := 0

topCopyLoop:
    cmp cx, 3       ; is cx == 3
    je exitCopy     ; if so, exit
    mov al, ds:[si] ; mov char into al
    mov ds:[di], al ; mov char in al into ds:[di] (the copiedLetters)
    inc si          ; go to next char in randomLetters
    inc di          ; go to next char in copiedLetters
    inc cx          ; inc cx
    jmp topCopyLoop

exitCopy:
    pop di si cx ax
    ret
copyChars ENDP
;============================

;============================
clearScreen PROC
    push si
    mov si, 0

topClearLoop:
    cmp si, 4000
    je exitClear
    mov es:[si], byte ptr ' '
    mov es:[si+1], byte ptr 00001111b
    add si, 2
    jmp topClearLoop

exitClear:
    pop si
    ret
clearScreen ENDP
;============================

;============================
;on entry:
;       look at the CARRY flag, if its true, then that means the player typed a correct char

handleGameState PROC
    jc foundCorrectChar ; if the carry flag is true, that means the player typed a good character
    mov numCorrect, 0   ; if they didn't, then reset the num correct
    call copyChars      ; reset all typed chars
    ret

foundCorrectChar:
    inc numCorrect      ; increment numCorrect
    cmp numCorrect, 3   ; is there 3 correct?
    je playerWin        ; if so, the player wins
    ret                 ; if not, return

playerWin:              
    call endGame        ; end the game
    ret
handleGameState ENDP
;============================

;============================
handleKey PROC
; on entry:
;   AL: contains the character we are looking for
; on exit:
;   Carry flag will be true if its found
    push cx di

    lea di, copiedLetters   ; di will contain address for copiedLetters
    mov cx, 0

topHandleKey:
    cmp cx, 3
    je keyNotFound ; if we reached the end and we haven't found anything, return with carry flag = 0
    and al, 11011111b
    cmp al, ds:[di] ; is the letter at di the same as what we typed?
    je keyFound     ; if so, then go to charFound
    inc di
    inc cx
    jmp topHandleKey

keyFound:
    mov ds:[di], byte ptr 0 ; set the char at this position to 0
    pop di cx
    stc
    call handleGameState
    ret

keyNotFound:
    pop di cx
    clc
    call handleGameState
    ret
handleKey ENDP
;============================

;============================
pickRandomChars PROC
    push ax bx si di
    mov bx, 0   ; this keeps count of how many successful characters have been pushed into 'randomLetters'
    lea si, randomLetters   ; pointing to the first character at 'randomLetters'
    mov di, 160

topPickChar:
    cmp bx, 3
    je exitPickRandomChar
    push word ptr 26    ; high range of getRandomNumber
    call getRandomNumber; puts random number 0-51 in AX
    add sp, 2           ; "clears" stack
    mov ah, 0           ; clear ah
    add al, 'A'         ; else, add 'A'

    call letterInList   ; AX will contain 1 if the letter is already in the 'randomLetters'
    jc topPickChar      ; if the carry flag is true, then that means we found that character, jump back to top
    mov ds:[si], al
    inc si
    inc bx
    jmp topPickChar

exitPickRandomChar:
    pop di si bx ax
    ret
pickRandomChars ENDP
;============================

;============================
letterInList PROC
; on entry:
;   AL: contains the character we are looking for
; on exit:
;   Carry flag will be true if its found
    cmp al, randomLetters
    je foundDuplicate
    cmp al, randomLetters+1
    je foundDuplicate
    cmp al, randomLetters+2
    jne notFound

foundDuplicate:
    stc
    ret

notFound:
    clc
    ret
letterInList ENDP
;============================

;============================
getRandomNumber PROC
    push bp
    mov bp, sp
    push bx cx dx

    mov ah, 00h ; stores low order of ticks in DX
    int 1ah
    mov ax, dx
    mov bx, seed    ; move the previous seed into bx
    mul bx          
    add ax, 1123    ; add large prime number
    ; the process above is equal to: a*r0+b where a and b are large prime numbers
    
    mov dx, 0       ; clear dx of any thing to allow for a divide without overflow errors
    mov bx, [bp+4]  ; move the high bound into bl
    div bx          ; divide ax by the high bound
    mov ax, dx      ; put remainder into ax
    mov seed, ax    ; make the seed the last result

    pop dx cx bx bp
    ret
getRandomNumber ENDP
;============================

;============================
scatterChars PROC
    push ax cx di
    mov cl, 0
    lea si, copiedLetters
topScatter:
    cmp cl, 3
    je exitScatter
    push word ptr 1920
    call getRandomNumber
    add sp, 2
    shl ax, byte ptr 1
    mov di, ax
    mov al, ds:[si]
    mov es:[di], al
    inc cx
    inc si
    jmp topScatter

exitScatter:
    pop di cx ax
    ret
scatterChars ENDP
;============================

;============================
waitTime PROC
    push ax bx cx dx

    mov ah, 00h  
    int 1ah         ; gets ticks CX:DX
                    ; dx has low order
    mov targetWaitTime, dx  ; make that our BASE target time
    push word ptr 30
    call getRandomNumber    ; ax will contain 0 - 30
    add sp, 2
    add ax, 30              ; ax will contain 30 - 60 tenths
    add targetWaitTime, ax

topWaitLoop:
    mov ah, 00h
    int 1ah         ; gets ticks CX:DX

    cmp dx, targetWaitTime 
    jge exitWaitLoop
    jmp topWaitLoop

exitWaitLoop:
    pop dx cx bx ax
    ret
waitTime ENDP
;============================

;============================
startTimer PROC
    push cx ax dx
    mov ah, 00h
    int 1ah             ; get current ticks into dx
    mov startTime, dx   ; move dx into startTime
    pop dx ax cx
    ret
startTimer ENDP
;============================

;============================
updateTimer PROC
    push ax

    push startTime          ; else, push the startTime
    call getTimeLapseTenths ; returns, in ax, the tenths of seconds since start time
    push ax                 ; push those tenths of seconds onto stack as an argument for showing time
    call showTime           ; call show time

    pop ax
    ret
updateTimer ENDP
;============================

;============================
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
;============================

;============================
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
    mov es:[160*24], dl ; put the character into that position
    mov si, 2           ; next position

topShowLoop:
    cmp si, 12      ; are we at the last printing position
    je exitShow     ; if so, exit
    cmp si, 8       ; are we at the position for the decimal
    je putDecimal   ; if so, do the decimal
    pop dx          ; else, pop the value at the current position in the timer
    mov es:[160*24+si], dl  ;print the value on the screen position
    add si, 2       ; next screen position
    jmp topShowLoop ; back to top

putDecimal:
    mov es:[160*24+si], byte ptr '.'
    add si, 2
    jmp topShowLoop

exitShow:
    pop si dx bx ax bp
    ret 2

showTime ENDP
;============================

myCode ENDS
;=========END OF SEGMENT=====

end main
