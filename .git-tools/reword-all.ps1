param(
    [Parameter(Mandatory=$true, Position=0)]
    [string]$TodoPath
)

$content = Get-Content -Raw -LiteralPath $TodoPath
$content = ($content -split "`n") | ForEach-Object {
    if ($_ -match '^[\t ]*pick[\t ]+[0-9a-f]{7,40}[\t ]+') {
        $_ -replace '^[\t ]*pick', 'reword'
    } else { $_ }
} | Out-String

# Out-String adds an extra newline at the end; trim it
$content = $content.TrimEnd("`r","`n")
Set-Content -LiteralPath $TodoPath -Value $content -NoNewline
exit 0

