Param(
    [Parameter(Mandatory=$true)][string]$Path
)

# Switch the target commit from pick to reword in the todo list
$content = Get-Content -Raw -Path $Path
# Match abbreviated hash form in the todo (e.g., 3055b79)
$pattern = '^pick\s+(3055b79[0-9a-f]*)'
$replacement = 'reword $1'
$content = [System.Text.RegularExpressions.Regex]::Replace($content, $pattern, $replacement, 'IgnoreCase, Multiline')

# Write back without BOM
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText($Path, $content, $utf8NoBom)
