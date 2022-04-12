; AUTHOR: TRISTAN ASBURY
; STRESS: HIGH
; FEELING AFTER GETTING IT DONE: EUPHORIC
; STACK SEGMENT

myStack SEGMENT STACK

    DW 256 DUP(?)

myStack ENDS

; DATA SEGMENT
myData SEGMENT

    buff DB 64 DUP (?)
    currBuffOffset DW $
    numBytesRead DW ?

    commandTail DB 128 DUP (?)
    
    inFileHandle DW ?

    minNumber DW ?


myData ENDS

; CODE SEGMENT

myCode SEGMENT
    assume ds: myData, cs: myCode

;==========MAIN PROC======================
main PROC

    mov ax, myData      ; Make DS point to data segment
	mov ds, ax

    call copyCommandTail
    call getFileNameAndNumber

    mov ax, 0b800h;
    mov es, ax

    lea di, commandTail
    mov si, 320

topLoop:
    mov al, ds:[di]
    cmp al, 0
    je exit
    mov es:[si], byte ptr al
    mov es:[si+1], byte ptr 00001111b
    add si, 2
    inc di
    jmp topLoop
    
    ; copy command tail into data seg

    ;

exit:
    mov AH, 4Ch     ; These two instructions use a DOS interrupt to give control back to OS
    int 21h
main ENDP
;=========================================

;=========================================
copyCommandTail PROC
    push ax cx si di
    
    mov cl, es:[80h]    ; cl contains the number of chars in the tail
    mov ch, 0
    dec cx

    mov si, 81h         ; si contains the actual address of the first character of command tail
    inc si              ; skip the first space

    lea di, commandTail ; di will contain the address of commandTail

topCmdTailLoop:
    cmp cx, 0           ; are we at the end?
    je belowCmdTail     ; if so, exit
    mov al, es:[si]     ; mov into al, the character at es:[si]
    mov ds:[di], al     ; mov the char into commandTail var
    inc si              ; move to next char pos
    inc di              ; move to next char pos
    dec cx              ; dec cx
    jmp topCmdTailLoop  ; go back to top

belowCmdTail:
    mov ds:[di], byte ptr 0

    pop di si cx ax
    ret
copyCommandTail ENDP
;=========================================



myCode ENDS

END main 
