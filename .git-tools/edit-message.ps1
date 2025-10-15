Param(
    [Parameter(Mandatory=$true)][string]$Path
)

# Overwrite the commit message for the reword step
$newMessage = "chore: Update .gitignore entries"

# Write back without BOM
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText($Path, $newMessage, $utf8NoBom)
