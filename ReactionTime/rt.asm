;=================
myStack SEGMENT STACK
    DB 256 DUP ?
myStack ENDS
;=================

;=================
myData SEGMENT
    randomLetters DW 3 DUP ?
    
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
    push ax

    push byte ptr 26
    call getRandomNumber
    add sp, 1

    add al, 'a'
    mov es:[160], byte ptr al
    mov es:[161], byte ptr 00001111b

    pop ax
    ret
pickRandomChar ENDP
;=====================

;=========================================
getRandomNumber PROC
    push bp
    mov bp, sp
    push bx

    mov ah, 00h
    int 1ah     ; get the current ticks

    mov bl, [bp+3]  ; mov the max bound into bl

    mov ax, dx  ; ax now contains the low end of ticks
    mov ah, 0   ; put 0 into ah, low low end of ticks
    div bl      ; divide ax by the upper limite

    pop bx bp
    ret
getRandomNumber ENDP
;=========================================

myCode ENDS

end main
