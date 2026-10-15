import { useState } from 'react'

function initials(name) {
  const words = String(name || 'School ERP').trim().split(/\s+/).filter(Boolean)
  return (words.length > 1 ? words.map((word) => word[0]) : words[0]?.slice(0, 3) || 'ERP')
    .join('').slice(0, 3).toUpperCase()
}

function logoUrl(path) {
  const value = String(path || '').trim()
  if (!value || value === 'null' || value === 'undefined') return ''
  return value
}

export default function SchoolBrand({ schoolName, logoPath, onClick, className = '', markClassName = 'w-9 h-9' }) {
  const [failed, setFailed] = useState(false)
  const name = String(schoolName || 'School ERP').trim() || 'School ERP'
  const logo = logoUrl(logoPath)
  const content = logo && !failed ? (
    <img src={logo} alt={`${name} logo`} className="h-full w-full rounded-lg object-contain bg-white" onError={() => setFailed(true)} />
  ) : initials(name)

  return (
    <div className={`flex items-center gap-2 min-w-0 ${className}`}>
      <div onClick={onClick} role={onClick ? 'button' : undefined} tabIndex={onClick ? 0 : undefined}
        className={`${markClassName} flex-shrink-0 rounded-lg bg-white text-primary-900 flex items-center justify-center font-bold text-[12px] overflow-hidden ${onClick ? 'cursor-pointer' : ''}`}>
        {content}
      </div>
      <span className="font-bold text-[14.5px] text-white whitespace-nowrap overflow-hidden text-ellipsis min-w-0">{name}</span>
    </div>
  )
}
