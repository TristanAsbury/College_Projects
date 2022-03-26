;=================
myStack SEGMENT STACK
    DB 256 DUP (?)
myStack ENDS
;=================

;=================
myData SEGMENT
    randomLetters DB 3 DUP (?)
    typedLetters DB 3 DUP (?)
    seed DW 1
    targetWaitTime DW (?)
    
myData ENDS
;=================

myCode SEGMENT
    assume ds: myData, cs: myCode

main PROC
    mov ax, 0b800h
    mov es, ax
    mov ax, myData
    mov ds, ax

    ; clear screen
    call clearScreen
    call pickRandomChar
    call waitTime
    call scatterChars

topMainLoop:

    mov ah, 4ch
    int 21h

main ENDP

;=====================
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
;=====================

;=====================
pickRandomChar PROC
    ; use the random number generator
    ; compare that character to the characters currently in randomLetters (using a loop)
    ; if that character is already there, then find another random number, keep doing this

    ; once we have a new character, then display it somewhere random on the screen
    ; do this by using the random number generator, 
    push ax bx si di
    mov bx, 0   ; this keeps count of how many successful characters have been pushed into 'randomLetters'
    lea si, randomLetters   ; pointing to the first character at 'randomLetters'
    mov di, 160

topPickChar:
    cmp bx, 3
    je exitPickRandomChar
    push word ptr 26    ; high range of getRandomNumber
    call getRandomNumber; puts random number 0-25 in AX
    add sp, 2           ; "clears" stack
    mov ah, 0           ; clear ah
    add al, 'a'         ; adds 'a' so we can get the actual character
    call letterInList   ; AX will contain 1 if the letter is already in the 'randomLetters'
    jc topPickChar      ; if the carry flag is true, then that means we found that character, jump back to top
    mov ds:[si], al
    inc si

    inc bx
    jmp topPickChar

exitPickRandomChar:
    pop di si bx ax
    ret
pickRandomChar ENDP
;=====================

;=====================
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
;=====================

;=====================
getRandomNumber PROC
    push bp
    mov bp, sp
    push bx

    mov ah, 00h ; stores low order of ticks in DX
    int 1ah

    mov ax, dx
    mov bx, seed    ; move the previous seed into bx
    mul bx          
    add ax, 1123    ; add large prime number

    ; the process above is equal to: a*r0+b where a and b are large prime numbers
    
    mov dx, 0       ; clear dx of any thing to allow for a divide

    mov bx, [bp+4]  ; move the high bound into bl
    div bx          ; divide ax by the high bound

    mov ax, dx  ; put remainder into ax
    mov seed, ax    ; make the seed the last result

    pop bx bp
    ret
getRandomNumber ENDP
;=====================

;=====================
scatterChars PROC
    push ax di
    ; MAKE THIS INTO A LOOP
    push word ptr 2000
    call getRandomNumber
    add sp, 2
    shl ax, byte ptr 1
    mov di, ax
    mov al, randomLetters
    mov es:[di], al
    
    push word ptr 2000
    call getRandomNumber
    add sp, 2
    shl ax, byte ptr 1
    mov di, ax
    mov al, randomLetters+1
    mov es:[di], al
    
    push word ptr 2000
    call getRandomNumber
    add sp, 2
    shl ax, byte ptr 1
    mov di, ax
    mov al, randomLetters+2
    mov es:[di], al

    pop di ax
    ret
scatterChars ENDP
;=====================

;=====================
waitTime PROC
    push ax bx cx

    mov ah, 00h  
    int 1ah         ; gets ticks CX:DX
    mov ax, dx      ; ax contains low order of ticks

    mov bx, 55      ; mov 55 into bx
    mul bx          ; multiply by 55 to get milliseconds
    
    mov bx, word ptr 100
    div bx          ; divide by 100 to get 10ths of seconds
    mov cx, ax      ; cx now contains the 10ths of seconds

    push word ptr 30
    call getRandomNumber ; ax will contain 0 - 30
    add ax, 30           ; ax will contain 30 - 60 (3 to 6 seconds)
    add cx, ax
    mov targetWaitTime, cx

topWaitLoop:
    mov ah, 00h  
    int 1ah         ; gets ticks CX:DX
    mov ax, dx      ; ax contains low order of ticks
    mov bx, 55      ; mov 55 into bx
    mul bx          ; multiply by 55 to get milliseconds
    mov bx, 100
    div bx          ; divide by 100 to get 10ths of seconds
    cmp targetWaitTime, ax
    jge exitWaitLoop
    jmp topWaitLoop

exitWaitLoop:
    pop cx bx ax
    ret
waitTime ENDP
;=====================


myCode ENDS

end main
