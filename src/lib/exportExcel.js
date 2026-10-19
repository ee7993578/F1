const escapeHtml = value => String(value ?? '')
  .replace(/&/g, '&amp;')
  .replace(/</g, '&lt;')
  .replace(/>/g, '&gt;')
  .replace(/"/g, '&quot;')

/**
 * Legacy ASPX reports used GridView.RenderControl with an Excel MIME type.
 * Keep that behaviour in the React migration: the downloaded .xls opens in
 * Excel and contains exactly the rows currently visible after filtering.
 */
export function downloadExcel(filename, rows, columns) {
  if (!Array.isArray(rows) || !rows.length) return false
  const cols = columns?.length
    ? columns
    : Object.keys(rows[0]).map(key => ({ key, label: key }))
  const header = cols.map(col => `<th>${escapeHtml(col.label ?? col.key)}</th>`).join('')
  const body = rows.map(row => `<tr>${cols.map(col => `<td>${escapeHtml(typeof col.value === 'function' ? col.value(row) : row[col.key])}</td>`).join('')}</tr>`).join('')
  const html = `<html><head><meta charset="utf-8"><style>table{border-collapse:collapse}th,td{border:1px solid #999;padding:4px 8px}th{font-weight:700;background:#e9ecef}</style></head><body><table><thead><tr>${header}</tr></thead><tbody>${body}</tbody></table></body></html>`
  const blob = new Blob([html], { type: 'application/vnd.ms-excel;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const anchor = document.createElement('a')
  anchor.href = url
  anchor.download = filename.toLowerCase().endsWith('.xls') ? filename : `${filename}.xls`
  anchor.click()
  URL.revokeObjectURL(url)
  return true
}
