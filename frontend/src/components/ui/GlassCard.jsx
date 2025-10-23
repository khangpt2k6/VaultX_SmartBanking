import React from "react";

/**
 * Reusable Glass Card Component
 * Provides glassmorphic container with consistent styling
 */
export const GlassCard = ({
  children,
  className = "",
  size = "md",
  hover = true,
  ...props
}) => {
  const sizeClasses = {
    sm: "p-3",
    md: "p-4",
    lg: "p-6",
  };

  return (
    <div
      className={`
        card-glass 
        ${sizeClasses[size]} 
        ${hover ? "hover:shadow-lg" : ""}
        ${className}
      `}
      {...props}
    >
      {children}
    </div>
  );
};

/**
 * Stat Card Component
 * Specialized glass card for displaying statistics
 */
export const StatCard = ({
  icon: Icon,
  label,
  value,
  badge,
  className = "",
  ...props
}) => {
  return (
    <div
      className={`stat-card-glass animate-fade-in-up ${className}`}
      {...props}
    >
      <div className="stat-header">
        {Icon && (
          <div className="stat-icon">
            <Icon size={24} color="white" />
          </div>
        )}
        {badge && <span className="badge-glass">{badge}</span>}
      </div>
      <div className="stat-value">{value}</div>
      <div className="stat-label">{label}</div>
    </div>
  );
};

export default GlassCard;
